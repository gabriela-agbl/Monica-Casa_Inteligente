package com.monica.casa.security;

<<<<<<< HEAD
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
=======
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // habilita @PreAuthorize nos controllers
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
<<<<<<< HEAD
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Se usar H2 em testes
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Restrito a Admin
                .requestMatchers("/api/dispositivos/**").hasAnyRole("ADMIN", "COMUM") // Admin e Comum
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {}); // Ou configuração de JWT posteriormente
=======
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/rotinas/**", "/api/alertas/**").hasRole("ADMIN")
                .requestMatchers("/api/dispositivos/**").hasAnyRole("ADMIN", "COMUM")
                // /api/auth/me, /api/dashboard e o resto: qualquer usuário autenticado
                .anyRequest().authenticated()
            )
            // O entry point customizado devolve 401 JSON sem o header WWW-Authenticate,
            // evitando o pop-up nativo de login do navegador no front-end React.
            .httpBasic(basic -> basic.authenticationEntryPoint(entryPointJson()));
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)

        return http.build();
    }

<<<<<<< HEAD
=======
    private AuthenticationEntryPoint entryPointJson() {
        return (request, response, ex) -> {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(
                "{\"status\":401,\"erro\":\"Unauthorized\",\"mensagem\":\"E-mail ou senha inválidos, ou login necessário.\"}");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${monica.cors.allowed-origins:http://localhost:5173,http://localhost:3000}") List<String> origens) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(origens);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 8adff7e (Implementa backend Spring Boot da Monica Casa Inteligente)
