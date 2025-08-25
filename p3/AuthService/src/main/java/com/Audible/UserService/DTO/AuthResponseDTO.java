package com.Audible.UserService.DTO;

import com.Audible.UserService.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
	private String token;
	private user userData;

}
