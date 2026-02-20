package dev.andresbonelli.productcomparisonapi.api.filter;

import dev.andresbonelli.productcomparisonapi.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class ApiKeyFilter extends OncePerRequestFilter {
    private final ApiKeyService apiKeyService;

    public ApiKeyFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String headerKey = request.getHeader("X-API-KEY");

        if (headerKey != null) {
            apiKeyService.validate(headerKey).ifPresent(apiKey -> {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + apiKey.getRole()));
                var auth = new UsernamePasswordAuthenticationToken(apiKey.getKeyValue(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }

        chain.doFilter(request, response);
    }
}
