package com.modris.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectConfig {

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception{
		
		http.httpBasic(Customizer.withDefaults());
		
		//http.csrf(c->c.disable());
		http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
		
		http.formLogin(
				c->c.defaultSuccessUrl("/",true)
				.loginPage("/login")
				.permitAll()
			
				);
		
		
		return http.build();
		
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	  return NoOpPasswordEncoder.getInstance();
	}
}
