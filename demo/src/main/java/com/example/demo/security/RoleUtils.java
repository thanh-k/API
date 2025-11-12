package com.example.demo.security;

import com.example.demo.entity.User;
public class RoleUtils {
  public static boolean isAdmin(User u){ return u.getRole() == User.Role.ADMIN; }
  public static boolean isStaff(User u){ return u.getRole() == User.Role.STAFF; }
}