package com.monica.casa;

import com.jayway.jsonpath.JsonPath;
import com.monica.casa.service.EvolutionApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Testa regras de acesso (ADMIN x COMUM), validação e o fluxo dispositivo -> rotina -> histórico. */
@SpringBootTest
class MonicaApiTest {

    @Autowired
    WebApplicationContext context;

    @MockitoBean
    EvolutionApiService evolutionApiService; // evita chamadas HTTP reais ao WhatsApp

    MockMvc mvc;

    final RequestPostProcessor admin = httpBasic("admin@monica.local", "admin123");

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void semCredenciaisRetorna401() throws Exception {
        mvc.perform(get("/api/dispositivos")).andExpect(status().isUnauthorized());
    }

    @Test
    void textoEmCampoNumericoRetorna400() throws Exception {
        mvc.perform(get("/api/dispositivos/abc").with(admin)).andExpect(status().isBadRequest());
    }

    @Test
    void validacaoRetornaErrosPorCampo() throws Exception {
        mvc.perform(post("/api/dispositivos").with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"tipo\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.tipo").exists());
    }

    @Test
    void cadastroPublicoCriaComumQueNaoCriaDispositivo() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Visita","email":"visita@teste.com","senha":"123456","telefone":"(11) 98888-7777"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("COMUM"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        RequestPostProcessor visita = httpBasic("visita@teste.com", "123456");
        mvc.perform(get("/api/auth/me").with(visita)).andExpect(status().isOk());
        mvc.perform(post("/api/dispositivos").with(visita).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"X\",\"tipo\":\"OUTRO\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/rotinas").with(visita)).andExpect(status().isForbidden());
    }

    @Test
    void fluxoDispositivoRotinaHistoricoEPermissaoDeConvidado() throws Exception {
        long liberado = criarDispositivo("Luz da Sala", true);
        long restrito = criarDispositivo("Cofre", false);

        // rotina inválida (dia errado) -> 400
        mvc.perform(post("/api/rotinas").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(rotinaJson("Ruim", "22:00", "XYZ", liberado)))
                .andExpect(status().isBadRequest());

        // rotina válida + execução manual
        MvcResult res = mvc.perform(post("/api/rotinas").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(rotinaJson("Acender sala", "18:30", "seg, ter ,sex", liberado)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diasSemana").value("SEG,TER,SEX"))
                .andReturn();
        long rotinaId = id(res);

        mvc.perform(post("/api/rotinas/" + rotinaId + "/executar").with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));

        mvc.perform(get("/api/dispositivos/" + liberado + "/historico").with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].origem").value("ROTINA"))
                .andExpect(jsonPath("$[0].ligado").value(true));

        // convidado: pode no liberado, não pode no restrito
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Amigo","email":"amigo@teste.com","senha":"123456","telefone":"11977776666"}
                        """)).andExpect(status().isCreated());
        RequestPostProcessor amigo = httpBasic("amigo@teste.com", "123456");

        mvc.perform(patch("/api/dispositivos/" + liberado + "/status").with(amigo)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ligado\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));
        mvc.perform(patch("/api/dispositivos/" + restrito + "/status").with(amigo)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ligado\":true}"))
                .andExpect(status().isForbidden());
    }

    // ---------- helpers ----------

    private long criarDispositivo(String nome, boolean convidado) throws Exception {
        MvcResult res = mvc.perform(post("/api/dispositivos").with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + nome + "\",\"tipo\":\"ILUMINACAO\",\"comodo\":\"Sala\","
                                + "\"acessoConvidado\":" + convidado + "}"))
                .andExpect(status().isCreated())
                .andReturn();
        return id(res);
    }

    private String rotinaJson(String nome, String horario, String dias, long dispositivoId) {
        return "{\"nome\":\"" + nome + "\",\"horario\":\"" + horario + "\",\"diasSemana\":\"" + dias
                + "\",\"acao\":\"LIGAR\",\"dispositivoId\":" + dispositivoId + "}";
    }

    private long id(MvcResult res) throws Exception {
        Number n = JsonPath.read(res.getResponse().getContentAsString(), "$.id");
        return n.longValue();
    }
}
