package dev.andresbonelli.productcomparisonapi.config;

import dev.andresbonelli.productcomparisonapi.api.filter.ApiKeyFilter;
import dev.andresbonelli.productcomparisonapi.domain.entity.Role;
import dev.andresbonelli.productcomparisonapi.service.ApiKeyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiKeyService service) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/keys/generate").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/api/root/**").hasRole(Role.ROOT.name())
                        .requestMatchers("/api/products/**").hasAnyRole(
                                Role.ROOT.name(),
                                Role.ADMIN.name(),
                                Role.USER.name()
                        )
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new ApiKeyFilter(service), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
