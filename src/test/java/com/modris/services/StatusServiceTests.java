package com.modris.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import com.modris.model.Status;
import com.modris.repositories.StatusRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StatusServiceTests {
	
	@Autowired
	private StatusRepository statusRepository;
	
	@Autowired
	private StatusService statusService;

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0").withDatabaseName("testcontainers2")
			.withUsername("sa").withPassword("123");

	static {
		mySQLContainer.start();
	}
	
	@BeforeAll()
	void beforeAll() {
		//data.sql already saves 4 Status values.
	}
	
	@Test
	@Transactional
	@DisplayName("findByIdReturnStatus method Test. Happy flow.")
	public void findByIdReturnStatusTest() {
		//Ongoing Hiatus Completed Dropped
		Status ongoing = statusService.findByIdReturnStatus(1L);
		Status hiatus = statusService.findByIdReturnStatus(2L);
		Status completed = statusService.findByIdReturnStatus(3L);
		Status dropped = statusService.findByIdReturnStatus(4L);
		
		
		assertAll(
				()-> assertEquals("Ongoing", ongoing.getName()),
				()-> assertEquals("Hiatus", hiatus.getName()),
				()-> assertEquals("Completed", completed.getName()),
				()->assertEquals("Dropped", dropped.getName())
				);
	}
	
}
