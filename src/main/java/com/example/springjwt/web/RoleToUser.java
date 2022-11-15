package com.example.springjwt.web;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class RoleToUser {
    @NotNull
    private String username;
    @NotNull
    private String role;
}
