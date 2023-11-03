package com.modris.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.modris.model.Users;
import com.modris.repositories.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public Optional<Users> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
}
