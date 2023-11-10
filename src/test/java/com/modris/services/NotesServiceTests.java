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
import org.testcontainers.containers.MySQLContainer;

import com.modris.model.Notes;
import com.modris.model.Tracker;
import com.modris.repositories.NotesRepository;
import com.modris.repositories.TrackerRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotesServiceTests {
	
	@Autowired
	private NotesService notesService;
	
	@Autowired
	private NotesRepository notesRepository;
	
	@Autowired
	private TrackerRepository trackerRepository;

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0").withDatabaseName("testcontainers2")
			.withUsername("sa").withPassword("123");

	static {
		mySQLContainer.start();
	}
	
	@BeforeAll()
	void beforeAll() {
		Tracker t = trackerRepository.findByIdAndUserIdReturnTracker(1L, 1L); // John user, Pulp Fiction
		Notes n = new Notes("Default","Good movie.",t );
		Notes n2 = new Notes("Default2","Recommend",t );
		n.setLastModified(LocalDateTime.now());
		n2.setLastModified(LocalDateTime.now());
		notesRepository.save(n);
		notesRepository.save(n2);
		

		//addTracker method test.
	}
	
	@Test
	@Transactional
	@DisplayName("findAllById method test. Happy flow.")
	void findAllByIdTest() {
		List<Notes> notesList = notesService.findAllByTrackerId(1L);
		LocalDateTime createdOn = notesList.get(0).getCreatedOn();
		LocalDateTime lastModified = notesList.get(0).getLastModified();
		int createdOnYear = createdOn.getYear();
		int lastModifiedYear = lastModified.getYear();
		assertAll(
				() -> assertEquals(2, notesList.size()),
				() -> assertEquals("Default", notesList.get(0).getName()),
				() -> assertEquals("Default2", notesList.get(1).getName()),
				() -> assertEquals("Good movie.", notesList.get(0).getComments()),
				() -> assertEquals("Recommend", notesList.get(1).getComments()),
				() -> assertTrue(createdOnYear>2000),
				() -> assertTrue(lastModifiedYear>2000)
				);
	}
	
	@Test
	@Transactional
	@DisplayName("findById method test. Happy flow.")
	void findById2Test() {
	Notes a = notesService.findById(1L);
	Notes b = notesService.findById(2L);
	
	assertAll(
			() -> assertEquals("Default", a.getName()),
			() -> assertEquals("Default2", b.getName()),
			() -> assertEquals("Good movie.", a.getComments()),
			() -> assertEquals("Recommend", b.getComments())
			);
	}
	
	@Test
	@Transactional
	@DisplayName("deleteByTrackerId method test. Happy flow.")
	void deleteByTrackerIdTest() {
		
		notesService.deleteByTrackerId(1L);
		
		List<Notes> note = notesService.findAllByTrackerId(1L);
		
		assertAll(
				()-> assertEquals(true, note.isEmpty())
				);
		
	}
	
	@Test
	@Transactional
	@DisplayName("deleteByIdAndTrackerId method test. Happy flow.")
	void deleteByIdAndTrackerIdTest() {
		notesService.deleteByIdAndTrackerId(1L, 1L);
		
		Notes a = notesService.findById(1L);
		Notes b = notesService.findById(2L);
		assertAll(
				()-> assertEquals(true, a == null),
				()-> assertEquals("Default2", b.getName())
				);
	}
	@Test
	@Transactional
	@DisplayName("updateNoteHibernate method test. Happy flow.")
	void updateNoteHibernate() {
		Notes original = notesRepository.findById2(1L);
		assertTrue(original.getCreatedOn().getYear() > 2022);
		notesService.updateNoteHibernate(1L, "Notes1", "Bad movie.","2000-11-10T12:18:03","2003-11-10T12:18:03" );
		Notes updated = notesRepository.findById2(1L);
		int createdOnYear = updated.getCreatedOn().getYear();
		int lastModifiedYear  = updated.getLastModified().getYear();;

		assertAll(
				()-> assertEquals("Notes1", updated.getName()),
				()-> assertEquals("Bad movie.", updated.getComments()),
				() -> assertTrue(createdOnYear == 2000),
				() -> assertTrue(lastModifiedYear == 2003)
				);
		
	}
	//public void updateNoteHibernate(Long notesId,String notesNameEdited,String notesCommentEdited) {
		//Notes original = notesRepository.findById2(notesId);
		//original.setName(notesNameEdited);
		//original.setComments(notesCommentEdited);
		
		
}
