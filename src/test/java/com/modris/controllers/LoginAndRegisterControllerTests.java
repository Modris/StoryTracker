package com.modris.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.modris.Main;
import com.modris.configurations.ProjectConfig;
import com.modris.controller.LoginAndRegisterController;
import com.modris.security.JpaUserDetailsService;
import com.modris.services.RegistrationService;

@WebMvcTest(LoginAndRegisterController.class)
@ContextConfiguration(classes={Main.class,ProjectConfig.class})
@DisplayName("Login And Register Controller class mapping tests.")
public class LoginAndRegisterControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	JpaUserDetailsService jpaUserDetailsService;
	
	@MockBean
	private  RegistrationService registrationService;
	
	
	@Test
	@DisplayName("/login @GET mapping. permitAll works unauthorized")
	void loginUnauthorizedTest() throws Exception {
		
		mockMvc.perform(get("/login"))
					.andExpect(status().isOk())
					.andExpect(view().name("login.html"));
	}
	

 //logout not so simple as i thought. Spring Security does some stuff like clear security context etc. 
	@Test
	@DisplayName("@GET/register unauthorized works. PermitAll works.")
	void getRegisterPermitAllWorks() throws Exception{
		
		mockMvc.perform(get("/register"))
		.andExpect(status().isOk())
		.andExpect(view().name("register.html"));
	}
	//Selenium tests would be way better here. 
	@Test
	@DisplayName("@GET/register UsernameTaken test.")
	void getRegisterError() throws Exception{
		
		mockMvc.perform(get("/register?usernameTaken=Registration+failed.+Username+is+taken."))
						.andExpect(status().isOk())
						.andExpect(view().name("register.html"))
						.andExpect(model().attribute("usernameTaken", "Registration+failed.+Username+is+taken."));
	}
	@Test
	@DisplayName("@GET/register UsernameTaken test v2")
	void getRegisterErrorv2() throws Exception{
		
		mockMvc.perform(get("/register")
				.param("usernameTaken", "Registration failed. Username is taken."))
				.andExpect(status().isOk())
				.andExpect(view().name("register.html"))
				.andExpect(model().attribute("usernameTaken", "Registration failed. Username is taken."));
	}
	

	

	@Test
	@DisplayName("@POST/registerSave permitAll works.")
	void postRegisterSaveUnauthorizedTest() throws Exception{
		
		mockMvc.perform(post("/registerSave").with(csrf())
						.param("username", "user")
						.param("password", "123"))
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/login"));
	}
	
	@Test
	@DisplayName("@POST/registerSave usernameTaken.")
	void postRegisterSaveUnauthorizedTest2() throws Exception{
		when(registrationService.taken("user")).thenReturn(true);
		mockMvc.perform(post("/registerSave").with(csrf())
						.param("username", "user")
						.param("password", "123"))
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/register?usernameTaken=Registration+failed.+Username+is+taken."));
	}
	
	@Test
	@DisplayName("login.html formlogin test")
	void formLoginTest() throws Exception{

			mockMvc.perform(formLogin("/login")
					.user("abc")
					.password("123"))
					.andExpect(redirectedUrl("/login?error"));
	}
	
	//egh more sophisticated form login tests require custom security context class. 
	//I'd rather spend time on end-to-end test with containerized app running in docker
	// so i can test that users are indeed saved into the database rather than formlogin is working with mocks
	// but that's a future idea.
	
}
