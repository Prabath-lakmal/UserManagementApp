package com.example.UserManagementApp.JWT;

import com.example.UserManagementApp.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Long userId = null;
        String jwtToken = null;

        final String authHeader= request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")){
            jwtToken=authHeader.substring(7);
        }
        //if jwt token is null, need to check the cookie
        if (jwtToken == null){
            Cookie [] cookies = request.getCookies();
            if (cookies != null){
                for (Cookie cookie:cookies){
                    if ("JWT".equals(cookie.getName())){
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        //if not token found

        if (jwtToken == null){
            filterChain.doFilter(request, response);
            return;
        }

        //Extract the useId from jwt token
        userId = jwtService.extractUserId(jwtToken);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //Fetch user details

            var userDetails= userRepository.findById(userId)
                    .orElseThrow(()-> new RuntimeException("User not found"));

            // Validate token
            if (jwtService.isTokenValid(jwtToken, userDetails)){
                List<SimpleGrantedAuthority> authorities = userDetails.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
