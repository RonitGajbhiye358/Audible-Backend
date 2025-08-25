package com.Audible.UserService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// Injecting the custom UserDetailsService to fetch user data from database
	@Autowired
	private UserDetailsService userDetailsService;

	// Injecting the JWT filter that will intercept requests for JWT validation
	@Autowired
	private JwtFilter jwtFilter;

	// Configures the security filter chain to define endpoint access rules, CSRF, session policy, and JWT filter
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(customizer -> customizer.disable()) // Disable CSRF as JWT is used
				.authorizeHttpRequests(request -> request
						.requestMatchers("/auth/register", "/auth/login").permitAll() // Public endpoints
						.requestMatchers("/auth/getAllUsers").hasRole("ADMIN")       // Admin-only endpoint
						.anyRequest().authenticated())                               // All other endpoints require authentication
//		.formLogin(Customizer.withDefaults()) // (Optional) Enables form-based login if needed
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before default auth filter
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Use stateless session (JWT)
		// .httpBasic(Customizer.withDefaults()); // (Optional) Enables basic HTTP auth if needed
		return http.build();
	}

	// Configures the AuthenticationProvider using DaoAuthenticationProvider with BCrypt encoding
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

	// Defines the password encoder to use BCrypt hashing algorithm
	@Bean
	public PasswordEncoder password_Encoder() {
		return new BCryptPasswordEncoder();
	}

	// Retrieves the AuthenticationManager bean from the authentication configuration
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
