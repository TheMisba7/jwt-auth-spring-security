package com.example.springjwt.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor @NoArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    AuthenticationManager authManger;
    JwtUtil jwtUtil;
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken userToAuth =
                new UsernamePasswordAuthenticationToken(username,password);

        // authenticate user with DAO auth provider
        return authManger.authenticate(userToAuth);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //if authentication is successful we generate two tokens :
        //first token with a short period of time and the second token with long period
        User user = (User) authResult.getPrincipal();

        String jwtAccessToken = jwtUtil.generateTest(user,JwtUtil.TOKEN_EXPIRES_AT,request.getRequestURI().toString());
        String jwtAccessTokenRefresher = jwtUtil.generateTest(user,JwtUtil.REFRESHER_EXPIRES_AT,request.getRequestURI().toString());
        Map<String,String> idToken = new HashMap<>();
        idToken.put("access-token",jwtAccessToken);
        idToken.put("refresher-token",jwtAccessTokenRefresher);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),idToken);
        response.setHeader("Authorization",jwtAccessToken);
    }
}
