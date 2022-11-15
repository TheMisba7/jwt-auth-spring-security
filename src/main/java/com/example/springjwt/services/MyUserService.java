package com.example.springjwt.services;

import com.example.springjwt.Entities.AppRole;
import com.example.springjwt.Entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MyUserService implements UserDetailsService {
    @Autowired
    AccountService accountService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = accountService.loadUserByUsername(username);
        System.out.println(appUser.getUsername());
        System.out.println(appUser.getPassword());
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        List<AppRole> roles = (List<AppRole>) appUser.getAppRoles();
        for( AppRole r: roles){
            authorities.add(new SimpleGrantedAuthority(r.getName()));
        }
        return new User(appUser.getUsername(),appUser.getPassword(), authorities);
    }
}
