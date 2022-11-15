package com.example.springjwt.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springjwt.Entities.AppRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtUtil {


    public static final int TOKEN_EXPIRES_AT = 60 * 1000;
    public static final int REFRESHER_EXPIRES_AT = 5 * 60 * 1000;

    public String generateTest(User user, int expiresAt, String url) {
        Map<String,Object> roles = new HashMap<>();
        roles.put("roles",user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .addClaims(roles)
                .setIssuer(url)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiresAt))
                .signWith(SignatureAlgorithm.HS256,"this_is_so_secret")
                .compact();
    }



    protected Collection<GrantedAuthority> getAuthorities(String token) {
        Claims claim = getClaims(token);
        List<Object> roles = (List<Object>) claim.get("roles");
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if(roles!=null){
            for(Object r: roles)
            {
                authorities.add(new SimpleGrantedAuthority(r.toString()));
            }
        }
        return  authorities;
    }
    public boolean validate(String token) {

        if (getUsername(token) != null && isExpired(token)) {
            return true;
        }
        return false;
    }

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean isExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().after(new Date(System.currentTimeMillis()));
    }

    private Claims getClaims(String token) {
        ;
        return Jwts.parser().setSigningKey("this_is_so_secret").parseClaimsJws(token).getBody();
    }

}
