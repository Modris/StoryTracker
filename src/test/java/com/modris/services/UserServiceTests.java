package com.modris.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import com.modris.model.Users;

import jakarta.transaction.Transactional;
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTests {


	@Autowired
	private UserService userService;
	
	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0").withDatabaseName("testcontainers2")
			.withUsername("sa").withPassword("123");

	static {
		mySQLContainer.start();
	}
	

	@BeforeAll
	void beforeAll() {

		
	}
	
	@Test
	@Transactional
	@DisplayName("findByUsername method test. Happy flow.")
	void findByUsername() {
		
		Optional<Users> user = userService.findByUsername("Jake");
		Users userExtracted = user.get();
		assertAll(
				() -> assertEquals("Jake", userExtracted.getUsername()),
				() -> assertEquals("123", userExtracted.getPassword())
				);
	}
	@Test
	@Transactional
	@DisplayName("findByUsername method test. Negative flow. Null.")
	void findByUsernameNegative() {
		
		Optional<Users> user = userService.findByUsername("Jake123");
		assertAll(
				() -> assertEquals(true,user.isEmpty())
				);
	}
	@Test
	@Transactional
	@DisplayName("findByIdReturnUsers method test. Happy flow.")
	void findByIdReturnUsers() {
		
		Users user = userService.findByIdReturnUsers(2L);
		assertAll(
				() -> assertEquals("Jake", user.getUsername()),
				() -> assertEquals("123", user.getPassword())
				);
	}

}
