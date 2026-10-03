package com.example.sms.config;

import com.example.sms.service.JwtService;
import com.example.sms.service.UserService;
import com.example.sms.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userService) {

        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        String username = null;
        String token = null;

        // Check whether Authorization header contains Bearer token
        if (authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")) {

            token = authorizationHeader.substring(7);

            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                // Invalid token
            }
        }

        // Authenticate user if token is valid
        if (username != null
                && SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

            try {
                User user = userService.findByUsername(username);

                if (jwtService.isTokenValid(token, username)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    null
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }

            } catch (Exception e) {
                // Invalid user or token
            }
        }

        filterChain.doFilter(request, response);
    }
}