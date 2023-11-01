package com.modris.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.modris.model.Authority;
import com.modris.model.UserRoles;
import com.modris.model.Users;
import com.modris.repositories.AuthorityRepository;
import com.modris.repositories.UserRepository;
import com.modris.repositories.UserRolesRepository;

@Service
public class RegistrationService {

	private final UserRepository userRepository;
	private final UserRolesRepository userRolesRepository;
	private final AuthorityRepository authorityRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public RegistrationService(UserRepository userRepository, UserRolesRepository userRolesRepository,
			AuthorityRepository authorityRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.userRolesRepository = userRolesRepository;
		this.authorityRepository = authorityRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public boolean taken(String username) {
		Optional<Users> userRepo = userRepository.findByUsername(username);
		if(userRepo.isPresent()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void register(Users user) {
		
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		userRepository.save(user);
		Optional<Authority> manager = authorityRepository.findById(3L); //3L is Manager.
		Authority managerExtracted = manager.get();
		UserRoles userRoles = new UserRoles(managerExtracted,user);
		userRolesRepository.save(userRoles);
	
	}
}
