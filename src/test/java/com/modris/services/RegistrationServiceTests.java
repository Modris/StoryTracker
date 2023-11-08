package com.modris.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import com.modris.model.Users;
import com.modris.repositories.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegistrationServiceTests {
	
	@Autowired
	private  UserRepository userRepository;

	@Autowired
	private RegistrationService registrationService;
	
	

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0").withDatabaseName("testcontainers2")
			.withUsername("sa").withPassword("123");

	static {
		mySQLContainer.start();
	}
	
	@Test
	@Transactional
	@DisplayName("taken method test. Happy + Negative flow.")
	void takenTest() {
		final boolean answer = registrationService.taken("John");
		final boolean answer2 = registrationService.taken("Jake");
		final boolean answer3 = registrationService.taken("John1234");
		assertAll(
				()-> assertEquals(true, answer),
				()-> assertEquals(true, answer2),
				()-> assertEquals(false, answer3)
				);
	}

	@Test
	@Transactional
	@DisplayName("register method test. Happy + Negative flow.")
	void registerTest() {
		
		Users user = new Users("Cena","123");
		String passwordOriginal = user.getPassword();
		registrationService.register(user);
		
		Users userFromRepo = userRepository.findByIdReturnUsers(3L);

		assertAll(
				()-> assertEquals(false, userFromRepo == null),
				()-> assertEquals("Cena", userFromRepo.getUsername(), () -> "Username failed to be saved into database? Did we change data.sql? Cena should be 3L."),						
				()-> assertEquals(60, userFromRepo.getPassword().length(), () -> "Password should be encrypted and length 60")
				);
		

	}
}
