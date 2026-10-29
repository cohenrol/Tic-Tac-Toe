package ru.cohenrol.ap1jvt03.di;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.cohenrol.ap1jvt03.domain.token.JwtAuthentication;
import ru.cohenrol.ap1jvt03.domain.token.JwtProvider;
import ru.cohenrol.ap1jvt03.domain.token.JwtUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    )
        throws ServletException, IOException
    {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER)) {
            String token = header.substring(BEARER.length());
            if (jwtProvider.validateAccessToken(token)) {
                Claims claims = jwtProvider.getClaims(token);
                JwtAuthentication auth = jwtUtil.createAuthentication(claims);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}