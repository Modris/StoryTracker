package com.modris.services;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import com.modris.model.Categories;
import com.modris.model.Status;
import com.modris.model.Tracker;
import com.modris.repositories.CategoriesRepository;
import com.modris.repositories.StatusRepository;
import com.modris.repositories.TrackerRepository;

import jakarta.transaction.Transactional;
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TrackerServiceTests {

	@Autowired
	private TrackerRepository trackerRepository;
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	
	@Autowired
	private StatusRepository statusRepository;
	
	@Autowired
	private TrackerService trackerService;

	
	  @ServiceConnection static MySQLContainer mySQLContainer = new
	  MySQLContainer("mysql:8.1") .withDatabaseName("testcontainer")
	  .withUsername("sa") .withPassword("123");
	  
	  
	  static { mySQLContainer.start(); }
	 
	@BeforeAll
	 void beforeAll() {
		var c = new Categories("Movie");
		var s = new  Status("Completed");
		  Tracker pulp = new Tracker("Pulp Fiction", c, s, "Finished"); 
		 var c2 =  new Categories("Comic Book");
		 var s2 = new Status("Ongoing");
		  Tracker piece = new Tracker("One Piece", c2, s2, "Chapter 1030");
		  categoriesRepository.save(c);
		  categoriesRepository.save(c2);
		  statusRepository.save(s);
		  statusRepository.save(s2);
		  trackerService.addTracker(pulp);
		  trackerService.addTracker(piece);
		 
	}
	
	@Test
	@Transactional
	@DisplayName("TestService saving Tracker class into TestContainers MySQL with trackerRepository")
	public void trackerServiceSavingIntoDBTest() {
		System.out.println("Yes");
		List<Tracker> trackerList = trackerService.findAll();
		assertAll(
				()-> assertEquals(2, trackerList.size()),
				()->assertEquals("Pulp Fiction", trackerList.get(0).getName()),
				()-> assertEquals("Movie", trackerList.get(0).getCategory().getName()),
				()-> assertEquals("Completed", trackerList.get(0).getStatus().getName()),
				()-> assertEquals("One Piece", trackerList.get(1).getName()),
				()-> assertEquals("Comic Book", trackerList.get(1).getCategory().getName())
				);
		
	}
	
	
	
}
