package com.Audible.UserService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Audible.UserService.entity.user;
import com.Audible.UserService.entity.UserPrincipal;
import com.Audible.UserService.repository.userRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private userRepository repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		user user=repo.findByUsername(username);
		if(user == null) {
			 System.out.println("user not found");
			 throw new UsernameNotFoundException("user not found");
		}
		return new UserPrincipal(user);
	}

}
