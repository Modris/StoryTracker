package com.modris.security;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.modris.model.Users;
import com.modris.repositories.UserRolesRepository;

public class SecurityUser implements UserDetails{

	private final Users user;
	private final UserRolesRepository userRolesRepository;

	public SecurityUser(Users user, UserRolesRepository userRolesRepository) {
		this.user = user;
		this.userRolesRepository = userRolesRepository;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		
		var authorities = userRolesRepository.getAuthoritiesWithUsername(user.getUsername());
		List<GrantedAuthority> roleList = new ArrayList<>();
	

		
		for(var start : authorities) {
			
			if(start.getAuthority() !=null) {
				roleList.add(new SimpleGrantedAuthority(start.getAuthority()));
			} 
		}
		
		return roleList;
	}
	@Override
	  public boolean isAccountNonExpired() {
	    return true;
	  }

	  @Override
	  public boolean isAccountNonLocked() {
	    return true;
	  }

	  @Override
	  public boolean isCredentialsNonExpired() {
	    return true;
	  }

	  @Override
	  public boolean isEnabled() {
	    return true;
	  }
}
