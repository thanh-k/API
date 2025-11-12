package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends org.springframework.web.filter.OncePerRequestFilter {
  private final JwtService jwtService; private final CustomUserDetailsService userDetailsService;
  public JwtAuthenticationFilter(JwtService j, CustomUserDetailsService u){ this.jwtService=j; this.userDetailsService=u; }
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String header = req.getHeader("Authorization"); String token = null;
    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) token = header.substring(7);
    if (token==null) token = JwtCookieUtil.readJwt(req);
    if (token!=null) {
      try {
        String email = jwtService.getSubject(token);
        var ud = userDetailsService.loadUserByUsername(email);
        var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception ignored) { }
    }
    chain.doFilter(req, res);
  }
}