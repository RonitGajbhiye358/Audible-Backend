package com.Audible.UserService.service;

import java.util.List;
import java.util.Optional;

import com.Audible.UserService.DTO.AuthResponseDTO;
import com.Audible.UserService.entity.user;

public interface service {
	public user registerUser(user user);
	
	public List<user> getAllUsers();

	public AuthResponseDTO verify(user user);

	Optional<user> getUserByUsername(String username);

	void deleteUserById(Integer customerId);

	void updateUserRole(Integer customerId, String role);

	long getUserCount();

	Optional<Optional<user>> getUserByCustomerId(Integer customerId);
}
