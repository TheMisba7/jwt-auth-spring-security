package com.example.springjwt.services;

import com.example.springjwt.Entities.AppRole;
import com.example.springjwt.Entities.AppUser;

import java.util.List;

public interface AccountService {
    AppRole addNewRole(AppRole role);
    AppUser addNewUser(AppUser user);
    AppUser addRoleToUser(String user, String rolename);
    AppUser loadUserByUsername(String username);
    List<AppUser> listUsers();
}
