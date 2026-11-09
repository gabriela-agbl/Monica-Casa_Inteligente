package com.monica.casa.dto;

import com.monica.casa.model.Perfil;
import com.monica.casa.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class UsuarioDTO {

    private UsuarioDTO() {
    }

    private static final String REGEX_TELEFONE = "^[0-9()+\\-\\s]{10,20}$";
    private static final String MSG_TELEFONE = "Telefone inválido. Use apenas números, ex.: (11) 99999-9999";

    /** Auto-cadastro público: sempre cria perfil COMUM. */
    public record Registro(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 100, message = "Nome deve ter até 100 caracteres") String nome,
            @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 100) String email,
            @NotBlank(message = "Senha é obrigatória") @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha,
            @NotBlank(message = "Telefone é obrigatório") @Pattern(regexp = REGEX_TELEFONE, message = MSG_TELEFONE) String telefone) {
    }

    /** Criação feita por um ADMIN (pode definir o perfil). */
    public record CriacaoAdmin(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
            @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 100) String email,
            @NotBlank(message = "Senha é obrigatória") @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha,
            @NotBlank(message = "Telefone é obrigatório") @Pattern(regexp = REGEX_TELEFONE, message = MSG_TELEFONE) String telefone,
            @NotNull(message = "Perfil é obrigatório (ADMIN ou COMUM)") Perfil perfil) {
    }

    /** Atualização por ADMIN. A senha é opcional (null = mantém a atual). */
    public record Atualizacao(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
            @NotBlank(message = "Telefone é obrigatório") @Pattern(regexp = REGEX_TELEFONE, message = MSG_TELEFONE) String telefone,
            @NotNull(message = "Perfil é obrigatório (ADMIN ou COMUM)") Perfil perfil,
            @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha) {
    }

    /** Nunca expõe o hash da senha. */
    public record Resposta(Long id, String nome, String email, String telefone, Perfil perfil, LocalDateTime criadoEm) {
        public static Resposta de(Usuario u) {
            return new Resposta(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getPerfil(), u.getCriadoEm());
        }
    }
}
