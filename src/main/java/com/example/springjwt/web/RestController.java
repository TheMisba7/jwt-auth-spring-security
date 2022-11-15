package com.example.springjwt.web;

import com.example.springjwt.Entities.AppRole;
import com.example.springjwt.Entities.AppUser;
import com.example.springjwt.security.JwtUtil;
import com.example.springjwt.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@org.springframework.web.bind.annotation.RestController
@AllArgsConstructor
public class RestController {
    AccountService accountService;
    UserDetailsService userDetailsService;
    JwtUtil jwtUtil;



         @GetMapping("/users")
        public List<AppUser> listUsers()
        {
            return  accountService.listUsers();
        }

        @PostMapping("/users")
        public AppUser addUser(AppUser appUser)
            {
                return accountService.addNewUser(appUser);
            }

        @PostMapping("/roles")
        public AppRole addRole(AppRole appRole)
        {
            return accountService.addNewRole(appRole);
        }
        @PostMapping("/roleToUser")
        public AppUser addRoleTouser(RoleToUser roleToUser)
        {
            return accountService.addRoleToUser(roleToUser.getUsername(),roleToUser.getRole());
        }
        @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String authtoken = request.getHeader("Authorization");
            if(authtoken!=null && authtoken.startsWith("Bearer "))
            {
                try {
                    String jwtToken = authtoken.substring(7);
                    String username = jwtUtil.getUsername(jwtToken);
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    String jwtAccessToken = jwtUtil.generateTest((User) user,JwtUtil.TOKEN_EXPIRES_AT,request.getRequestURI().toString());
                    String jwtAccessTokenRefresher = jwtUtil.generateTest((User) user,JwtUtil.REFRESHER_EXPIRES_AT,request.getRequestURI().toString());
                    Map<String,String> idTolen = new HashMap<>();
                    idTolen.put("access-token",jwtAccessToken);
                    idTolen.put("refresher-token",jwtAccessTokenRefresher);
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(),idTolen);
                    response.setHeader("Authorization",jwtAccessToken);

                } catch (Exception e) {
                    //probably token expired -> redirect to Log In
                 throw new RuntimeException(e.getMessage());
                }
            }else {
                //redirect to somewhere
                throw new RuntimeException("Refresh token not valid");
            }
        }

}
