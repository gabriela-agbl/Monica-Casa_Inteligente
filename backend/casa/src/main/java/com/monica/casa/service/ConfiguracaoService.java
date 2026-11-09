package com.monica.casa.service;

import com.monica.casa.dto.ConfiguracaoDTO;
import com.monica.casa.exception.RecursoNaoEncontradoException;
import com.monica.casa.exception.RegraNegocioException;
import com.monica.casa.model.Configuracao;
import com.monica.casa.repository.ConfiguracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    public static final String NOME_CASA = "NOME_CASA";
    public static final String NOTIFICACOES_WHATSAPP = "NOTIFICACOES_WHATSAPP";
    public static final String NOTIFICAR_ROTINAS = "NOTIFICAR_ROTINAS";

    private final ConfiguracaoRepository repository;

    @Transactional(readOnly = true)
    public List<ConfiguracaoDTO.Resposta> listar() {
        return repository.findAllByOrderByChaveAsc().stream().map(ConfiguracaoDTO.Resposta::de).toList();
    }

    @Transactional(readOnly = true)
    public ConfiguracaoDTO.Resposta obter(String chave) {
        return ConfiguracaoDTO.Resposta.de(buscar(normalizarChave(chave)));
    }

    /** Cria ou atualiza (upsert) a configuração. */
    @Transactional
    public ConfiguracaoDTO.Resposta salvar(String chave, ConfiguracaoDTO.Requisicao req) {
        String k = normalizarChave(chave);
        Configuracao c = repository.findByChave(k).orElseGet(() -> {
            Configuracao nova = new Configuracao();
            nova.setChave(k);
            return nova;
        });
        c.setValor(req.valor().trim());
        if (req.descricao() != null) {
            c.setDescricao(req.descricao().trim());
        }
        return ConfiguracaoDTO.Resposta.de(repository.save(c));
    }

    @Transactional
    public void remover(String chave) {
        repository.delete(buscar(normalizarChave(chave)));
    }

    /** Cria a configuração apenas se ainda não existir (carga inicial). */
    @Transactional
    public void garantirPadrao(String chave, String valor, String descricao) {
        if (repository.findByChave(chave).isEmpty()) {
            Configuracao c = new Configuracao();
            c.setChave(chave);
            c.setValor(valor);
            c.setDescricao(descricao);
            repository.save(c);
        }
    }

    @Transactional(readOnly = true)
    public boolean getBoolean(String chave, boolean padrao) {
        return repository.findByChave(chave)
                .map(c -> Boolean.parseBoolean(c.getValor().trim()))
                .orElse(padrao);
    }

    private Configuracao buscar(String chave) {
        return repository.findByChave(chave)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Configuração não encontrada: " + chave));
    }

    private String normalizarChave(String chave) {
        String k = chave == null ? "" : chave.trim().toUpperCase(Locale.ROOT);
        if (!k.matches("[A-Z0-9_]{3,100}")) {
            throw new RegraNegocioException("Chave inválida. Use de 3 a 100 caracteres: letras, números e '_'.");
        }
        return k;
    }
}
