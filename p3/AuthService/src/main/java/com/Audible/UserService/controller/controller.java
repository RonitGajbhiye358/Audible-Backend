package com.Audible.UserService.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.Audible.UserService.DTO.AuthResponseDTO;
import com.Audible.UserService.entity.user;
import com.Audible.UserService.service.ServiceImpl;

@RestController
@RequestMapping("/auth")
public class controller {

	@Autowired
	private ServiceImpl service;
	
	@PostMapping("/login")
	public AuthResponseDTO login(@RequestBody user user) {
		return service.verify(user);
	}

	@PostMapping("/register")
	@ResponseStatus(code = HttpStatus.CREATED)
	public user registerCandidate(@RequestBody user user) {
		return service.registerUser(user);
	}

	
	// Not Useful 
	@GetMapping("/current-user")
	public String getLoggedInUser(Principal principal) {
		return principal.getName();
	}
		
	@GetMapping("/my-role")
    public String getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return "User Roles: " + roles;
    }
	
}
