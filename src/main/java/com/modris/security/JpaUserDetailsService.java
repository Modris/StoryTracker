package com.modris.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.modris.repositories.UserRepository;
import com.modris.repositories.UserRolesRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserRolesRepository userRolesRepository;
	
	public JpaUserDetailsService(UserRepository userRepository, UserRolesRepository userRolesRepository) {
		this.userRepository = userRepository;
		this.userRolesRepository = userRolesRepository;
	}
	
	@Override
	public UserDetails  loadUserByUsername(String username) throws UsernameNotFoundException{
		
		var user = userRepository.findByUsername(username);
		
		return user.map(u -> new SecurityUser(u,userRolesRepository))
				.orElseThrow( () -> new UsernameNotFoundException("No Such Username found."));
		
		
	}
}
