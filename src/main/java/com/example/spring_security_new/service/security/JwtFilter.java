package com.example.spring_security_new.service.security;

import com.example.spring_security_new.service.MyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    MyAuthService myAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        // REQUESTDAN TOKENNI OLISH
        String token = httpServletRequest.getHeader("Authorization");

        // TOKEN BORLIGINI VA TOKENNING BOSHLANISHI BEARER BO'LISHINI TEKSHIRYAPMIZ
        if (token != null && token.startsWith("Bearer")) {

            // AYNAN TOKENNI O'ZINI QIRQIB OLDIK
            token = token.substring(7);

            // TOKENNI VALIDATSIADAN O'TKAZDIK(BUNDA TOKEN BUZILMAGANLIGINI, MUDDATI O'TMAGANLIGINI VA H.K)
            boolean validateToken = jwtProvider.validateToken(token);

            if (validateToken) {
                // TOKENNI ICHIDAN USERNAME NI OLDIK
                String username = jwtProvider.getUsernameFromToken(token);

                // USERNAME ORQALI USERDETILSNI OLDIK
                UserDetails userDetails = myAuthService.loadUserByUsername(username);

                // USERDETAILS ORQALI AUTHHENTICATION YARATIB OLDIK
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                // SYSTEMAGA KIM KIRGANLIGINI O'RNATDIK
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
