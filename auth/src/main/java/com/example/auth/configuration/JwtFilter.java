package com.example.auth.configuration;

import com.example.auth.service.UserService;
import com.example.auth.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final String secretKey;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authentication = request.getHeader(HttpHeaders.AUTHORIZATION);
        logger.info("authen : "+ authentication);
        if (authentication == null || !authentication.startsWith("Bearer ")) {
            logger.info("authen이 없음 ");
            filterChain.doFilter(request, response);
            throw new RuntimeException("토큰 없음");
        }
        //만료 여부
        String token = authentication.split(" ")[1];
        if (JwtUtil.isExpired(token, secretKey)) {
            logger.info("token 만료");
            filterChain.doFilter(request, response);
            throw new RuntimeException("토큰 만료");

        }


        String userName = JwtUtil.getUserName(token, secretKey);
        logger.info("name : " + userName);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }
}
