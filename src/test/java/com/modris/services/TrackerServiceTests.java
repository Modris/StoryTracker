package com.modris.services;

import static org.junit.Assert.assertTrue;
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
import com.modris.model.Users;
import com.modris.repositories.CategoriesRepository;
import com.modris.repositories.StatusRepository;
import com.modris.repositories.TrackerRepository;
import com.modris.repositories.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TrackerServiceTests {

	@Autowired
	private TrackerRepository trackerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoriesRepository categoriesRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private TrackerService trackerService;

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0").withDatabaseName("testcontainers2")
			.withUsername("sa").withPassword("123");

	static {
		mySQLContainer.start();
	}

	@BeforeAll
	void beforeAll() {
		Users user = new Users("John", "123");
		userRepository.save(user);

		var c = new Categories("Movie");
		var s = new Status("Completed");
		Tracker pulp = new Tracker("Pulp Fiction", c, s, "Finished");
		pulp.setUser(user);
		var c2 = new Categories("Comic Book");
		var s2 = new Status("Ongoing");
		Tracker piece = new Tracker("One Piece", c2, s2, "Chapter 1030");
		piece.setUser(user);
		
		categoriesRepository.save(c);
		categoriesRepository.save(c2);
		statusRepository.save(s);
		statusRepository.save(s2);
		trackerService.addTracker(pulp);
		trackerService.addTracker(piece);
	}

	@Test
	@Transactional
	@DisplayName("TrackerService method addTracker + findAllByUsername happy flow. Saving into DB successful. TestContainers.")
	public void beforeAllIsSavingDataIntoDB() {
	
		List<Tracker> trackerList = trackerService.findAllByUsername("John");
		assertAll(() -> assertEquals(2, trackerList.size()),
				() -> assertEquals("Pulp Fiction", trackerList.get(0).getName()),
				() -> assertEquals("Movie", trackerList.get(0).getCategory().getName()),
				() -> assertEquals("Completed", trackerList.get(0).getStatus().getName()),
				() -> assertEquals("One Piece", trackerList.get(1).getName()),
				() -> assertEquals("Comic Book", trackerList.get(1).getCategory().getName()));

	}

	@Test
	@Transactional
	@DisplayName("TrackerService deleteByIdAndUserId test happy flow. Also findByIdAndUserId happy flow and null flow test.")
	public void trackerServiceDeleteByIdAndUserIdTest() {
		long sizeBefore = trackerRepository.findAll().spliterator().getExactSizeIfKnown();
		trackerService.deleteByIdAndUserId(2L,1L); //1L is "John"
		long sizeAfter = trackerRepository.findAll().spliterator().getExactSizeIfKnown();
		Tracker t = trackerService.findByIdAndUserId(1L,1L); //1L is John
		Tracker tNull = trackerService.findByIdAndUserId(1L,99L); // 99L doesn't exist.
 
		assertAll(
				() -> assertEquals(2, sizeBefore), () -> assertEquals(1, sizeAfter),
				() -> assertEquals("Pulp Fiction", t.getName()),
				() -> assertTrue(tNull == null)
				);
	}
	
	@Test
	@Transactional
	@DisplayName("TrackerService.editSaved method works. Happy flow.")
	public void trackerServiceEditSavedTest() {
		
		 
			Tracker original = trackerRepository.findByIdAndUserIdReturnTracker(2L, 1L);
			original.setName("One Piece 2");
			var c = new Categories("Movies");
			var s = new Status("Dropped");
			original.setCategory(c);
			original.setStatus(s);
			original.setProgress("Now2");
			trackerRepository.save(original);

		assertAll(() -> assertEquals("One Piece 2", trackerRepository.findByIdAndUserIdReturnTracker(2L,2L).getName()),
				() -> assertEquals("Movies", trackerRepository.findByIdAndUserIdReturnTracker(2L,2L).getCategory().getName()),
				() -> assertEquals("Dropped", trackerRepository.findByIdAndUserIdReturnTracker(2L,2L).getStatus().getName()),
				() -> assertEquals("Now2", trackerRepository.findByIdAndUserIdReturnTracker(2L,2L).getProgress()));
		
	
	}

}
