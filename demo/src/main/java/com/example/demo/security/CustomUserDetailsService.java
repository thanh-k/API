package com.example.demo.security;

import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepo;
  public CustomUserDetailsService(UserRepository r){ this.userRepo=r; }
  @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var u = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
    return new CustomUserDetails(u);
  }
}