package com.example.springjwt.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor @NoArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // find Authorization header, so we can know if we should execute this filter
        String authToken = request.getHeader("Authorization");
        if(authToken==null || authToken.isEmpty() || !authToken.startsWith("Bearer "))
        {   //if Authorization header does not exist then we skip this filter
            filterChain.doFilter(request,response);
            return;
        }

        //Authorization header does exist with jwt token -> check if token valid
        String token = authToken.substring(7);
        if(!jwtUtil.validate(token))
        {
            // if jwt token not valid, then skip this filter
            filterChain.doFilter(request,response);
            return;
        }



        // Authorization header exists, token is valid.
        // So, we can authenticate.
        String username = jwtUtil.getUsername(token);

        UsernamePasswordAuthenticationToken authUser =
                new UsernamePasswordAuthenticationToken(username, null, jwtUtil.getAuthorities(token));

        // set authenticated user to spring security context
        SecurityContextHolder.getContext().setAuthentication(authUser);
        //end of this method -> go to next filter class
        filterChain.doFilter(request, response);






    }
}
