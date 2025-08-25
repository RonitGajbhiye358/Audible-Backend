package com.Audible.UserService.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Audible.UserService.service.JwtService;
import com.Audible.UserService.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	// Autowires JwtService to handle token extraction and validation
	@Autowired
	private JwtService jwtService;
	
	// ApplicationContext is used to dynamically get beans, like MyUserDetailsService
	@Autowired
	private ApplicationContext context;

	// This method intercepts each HTTP request to perform JWT-based authentication
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		// Extract the Authorization header from the incoming request
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		
		// Check if the Authorization header contains a Bearer token and extract it
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtService.extractUsername(token);
		}
		
		// Proceed only if username is extracted and no authentication is currently set
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
			
			// Validate the token and set the authentication in the SecurityContext if valid
			if (jwtService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authToken =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		// Continue with the remaining filter chain
		filterChain.doFilter(request, response);
	}
}
