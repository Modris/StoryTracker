package com.modris.services;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.testcontainers.containers.MySQLContainer;

import com.modris.model.Categories;
import com.modris.model.Status;
import com.modris.model.Tracker;
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
	private CategoriesRepository categoriesRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private TrackerService trackerService;
	
	@Autowired
	private UserRepository userRepository;

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0").withDatabaseName("testcontainers2")
			.withUsername("sa").withPassword("123");

	static {
		mySQLContainer.start();
	}

	@BeforeAll
	void beforeAll() {
		
		/*
		data.sql will add the data required: 
		INSERT INTO categories VALUES
		(NULL,"Movies"),
		(NULL,"Books"),
		(NULL,"TV-Shows"),
		(NULL,"Comic books");
		
		INSERT INTO status VALUES(NULL,"Ongoing"),(NULL,"Hiatus"),
		(NULL,"Completed"),(NULL,"Dropped");
		
		INSERT INTO users VALUES(NULL,"John","123");
		INSERT INTO users VALUES(NULL,"Jake","123");
		
		INSERT INTO tracker VALUES(NULL, "Pulp Fiction", 1,3,"Finished",'2022-02-05 10:12:11','2022-02-05 10:12:11', 1);
		INSERT INTO tracker VALUES(NULL, "One Piece", 4,1,"Chapter 1030",'2022-02-05 10:12:11','2022-02-05 10:12:11', 1);
		
		// OLD DATE before data.sql 
		userRepository.save(user);
		Categories c = categoriesRepository.findByIdReturnCategories(1L); // Movies
		Status s = statusRepository.findByIdReturnStatus(3L); // Completed

		Tracker pulp = new Tracker("Pulp Fiction", c, s, "Finished");
		pulp.setUser(user);
		
		Categories c2 = categoriesRepository.findByIdReturnCategories(4L); // Comic books
		Status s2 = statusRepository.findByIdReturnStatus(1L); // Ongoing
		Tracker piece = new Tracker("One Piece", c2, s2, "Chapter 1030");
		
		piece.setUser(user);
		
		trackerService.addTracker(pulp);
		trackerService.addTracker(piece);
		*/
	}
	@Test
	@Transactional
	@DisplayName("TrackerService method addTracker test.")
	public void addTrackerTest() {
		Categories c = categoriesRepository.findByIdReturnCategories(1L); // Movies
		Status s = statusRepository.findByIdReturnStatus(3L); // Completed
		
		Tracker pulp = new Tracker("Pulp FictionX2", c, s, "FinishedX2");
		pulp.setUser(userRepository.findById(2L).get()); // Jake
		trackerService.addTracker(pulp);
		
		List<Tracker> trackerList = trackerService.findAllByUsernameNative("John");
		List<Tracker> trackerListJake = trackerService.findAllByUsernameNative("Jake");
		assertAll(
				() -> assertEquals(1, trackerListJake.size()),
				() -> assertTrue(trackerListJake.get(0).getCreatedOn().getYear() > 2000)
				);
	}
	@Test
	@Transactional
	@DisplayName("data.sql + findAllByUsername happy flow. Saving into DB successful. TestContainers.")
	public void beforeAllIsSavingDataIntoDB() {
	
		List<Tracker> trackerList = trackerService.findAllByUsernameNative("John");
		assertAll(() -> assertEquals(2, trackerList.size()),
				() -> assertEquals("Pulp Fiction", trackerList.get(0).getName()),
				() -> assertEquals("Movies", trackerList.get(0).getCategory().getName()),
				() -> assertEquals("Completed", trackerList.get(0).getStatus().getName()),
				() -> assertEquals("One Piece", trackerList.get(1).getName()),
				() -> assertEquals("Comic books", trackerList.get(1).getCategory().getName()));

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
				() -> assertEquals(2, sizeBefore),
				() -> assertEquals(1, sizeAfter),
				() -> assertEquals("Pulp Fiction", t.getName()),
				() -> assertTrue(tNull == null)
				);
	}
	@Test
	@Transactional
	@DisplayName("TrackerService.editSaved method works. Happy flow no changes.")
	public void trackerServiceEditSavedTestNoChanges() {
		
		 
			Tracker original = trackerRepository.findByIdAndUserIdReturnTracker(2L, 1L);

			trackerService.editSavedHibernate(original, 2L,1L);

		assertAll(
				() -> assertEquals("One Piece", trackerRepository.findByIdAndUserIdReturnTracker(2L,1L).getName()),
				() -> assertEquals("Comic books", trackerRepository.findByIdAndUserIdReturnTracker(2L,1L).getCategory().getName()),
				() -> assertEquals("Ongoing", trackerRepository.findByIdAndUserIdReturnTracker(2L,1L).getStatus().getName()),
				() -> assertEquals("Chapter 1030", trackerRepository.findByIdAndUserIdReturnTracker(2L,1L).getProgress()));
		
	
	}
	
	@Test
	@Transactional
	@DisplayName("TrackerService.editSaved method works. Happy flow Edited..")
	public void trackerServiceEditSavedTest() {
		//public void editSavedHibernate(Tracker tEdited,Long id, Long userId) {
			Tracker tEdited = trackerRepository.findByIdAndUserIdReturnTracker(2L, 1L);
			assertTrue(tEdited.getCreatedOn().getYear() == 2022);
			
			
			tEdited.setName("One Piece 2");
			Categories c = categoriesRepository.findByIdReturnCategories(1L); // Movies
			Status s = statusRepository.findByIdReturnStatus(4L); // Dropped
			tEdited.setCategory(c);
			tEdited.setStatus(s);
			tEdited.setProgress("Now2");
			tEdited.setCreatedOn(LocalDateTime.parse("2000-11-10T12:49:41"));
			tEdited.setLastModified(LocalDateTime.parse("2003-11-10T12:49:41"));
			trackerService.editSavedHibernate(tEdited, 2L,1L);
			Tracker updated = trackerRepository.findByIdAndUserIdReturnTracker(2L,1L);
		assertAll(
				() -> assertTrue(updated.getCreatedOn().getYear() == 2000),
				() -> assertTrue(updated.getLastModified().getYear() == 2003),
				() -> assertEquals("One Piece 2", updated.getName()),
				() -> assertEquals("Movies", updated.getCategory().getName()),
				() -> assertEquals("Dropped", updated.getStatus().getName()),
				() -> assertEquals("Now2", updated.getProgress()));
	}
	@Test
	@Transactional
	@DisplayName("findAllPagedWithUserId method happy flow test.")
	public void findAllPagedWithUserIdTest() {
		
		/*public Page<Tracker> findAllPagedWithUserId(Long userId, int pageNumber, int pageSize, String sortField, String sortDirection){
			Pageable pageable = PageRequest.of(pageNumber-1, pageSize,
					sortDirection.equals("asc") ? Sort.by(sortField).ascending()
							: Sort.by(sortField).descending());
			
			return trackerRepository.findAllPagedWithUserId(pageable,userId);
		*/
		//null doesn't matter because it won't display the table on thymeleaf page that's all.
		Page<Tracker> pagedAsc = trackerService.findAllPagedWithUserId(1L, 1, 1,"id" , "asc");
		List<Tracker> trackerList = pagedAsc.getContent();
		
		Page<Tracker> pagedDesc = trackerService.findAllPagedWithUserId(1L, 1, 1,"id" , "desc");
		List<Tracker> trackerListDesc = pagedDesc.getContent();
		
		Page<Tracker> pagedAll = trackerService.findAllPagedWithUserId(1L, 1, 10,"id" , "desc");
		List<Tracker> trackerListAll = pagedAll.getContent();
		assertAll(
				() -> assertEquals(1, trackerList.size()),
				() -> assertEquals("Pulp Fiction", trackerList.get(0).getName()),
				() -> assertEquals(1, trackerListDesc.size()),
				() -> assertEquals("One Piece", trackerListDesc.get(0).getName()),
				() -> assertEquals(2, trackerListAll.size()),
				() -> assertEquals("One Piece", trackerListAll.get(0).getName()),
				() -> assertEquals("Pulp Fiction", trackerListAll.get(1).getName())
				);
		
	}

}
