package com.monica.casa.config;

import com.monica.casa.model.Perfil;
import com.monica.casa.model.Usuario;
import com.monica.casa.repository.UsuarioRepository;
import com.monica.casa.service.ConfiguracaoService;
import com.monica.casa.util.TelefoneUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Carga inicial: cria o primeiro ADMIN (se não existir nenhum) e as configurações padrão.
 * Sem isso ninguém conseguiria logar, já que o cadastro público só cria perfil COMUM.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfiguracaoService configuracaoService;

    @Value("${monica.admin.nome:Morador Mônica}")
    private String adminNome;

    @Value("${monica.admin.email:admin@monica.local}")
    private String adminEmail;

    @Value("${monica.admin.senha:admin123}")
    private String adminSenha;

    @Value("${monica.admin.telefone:5511999999999}")
    private String adminTelefone;

    @Override
    public void run(String... args) {
        if (usuarioRepository.countByPerfil(Perfil.ADMIN) == 0) {
            Usuario admin = new Usuario();
            admin.setNome(adminNome);
            admin.setEmail(adminEmail.trim().toLowerCase());
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setTelefone(TelefoneUtil.normalizar(adminTelefone));
            admin.setPerfil(Perfil.ADMIN);
            usuarioRepository.save(admin);
            log.warn("Administrador inicial criado: {} (troque a senha padrão antes de usar fora da demo!)",
                    admin.getEmail());
        }

        configuracaoService.garantirPadrao(ConfiguracaoService.NOME_CASA, "Casa Mônica 2050",
                "Nome exibido no painel e nas mensagens");
        configuracaoService.garantirPadrao(ConfiguracaoService.NOTIFICACOES_WHATSAPP, "true",
                "Envia alertas ao WhatsApp dos administradores via Evolution API");
        configuracaoService.garantirPadrao(ConfiguracaoService.NOTIFICAR_ROTINAS, "true",
                "Avisa os administradores sempre que uma rotina automática for executada");
    }
}
