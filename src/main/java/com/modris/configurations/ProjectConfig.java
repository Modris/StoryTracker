package com.modris.configurations;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.modris.security.JpaUserDetailsService;

@Configuration
@EnableMethodSecurity
public class ProjectConfig {

	
	private final JpaUserDetailsService jpaUserDetailsService;
	
	public ProjectConfig(JpaUserDetailsService jpaUserDetailsService) {
		this.jpaUserDetailsService = jpaUserDetailsService;
	}
	
	
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception{
		
		http.httpBasic(Customizer.withDefaults());
		
		//http.csrf(c->c.disable());
		http.authenticationProvider(authProvider());

		http.authorizeHttpRequests(
				c -> c.requestMatchers("/register").permitAll()
				.requestMatchers("registerSave").permitAll()
				.anyRequest().authenticated());
		
		http.formLogin(
				c->c.defaultSuccessUrl("/",true)
				.loginPage("/login")
				.permitAll()
				);
		
		return http.build();
		
	}
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(bCryptpasswordEncoder());
        return authProvider;
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptpasswordEncoder() {
		return new BCryptPasswordEncoder(10, new SecureRandom());
	}
}
