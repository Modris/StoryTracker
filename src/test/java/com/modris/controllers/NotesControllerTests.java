package com.modris.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.modris.Main;
import com.modris.configurations.ProjectConfig;
import com.modris.controller.NotesController;
import com.modris.model.Categories;
import com.modris.model.Notes;
import com.modris.model.Status;
import com.modris.model.Tracker;
import com.modris.model.Users;
import com.modris.security.JpaUserDetailsService;
import com.modris.services.NotesService;
import com.modris.services.TrackerService;
import com.modris.services.UserService;

@WebMvcTest(NotesController.class)
@ContextConfiguration(classes={Main.class,ProjectConfig.class})
@DisplayName("Notes Controller class mapping tests.")
public class NotesControllerTests {

	@Autowired
    private MockMvc mockMvc;

	@MockBean
	JpaUserDetailsService jpaUserDetailsService;
	
	@MockBean
	private  NotesService notesService;
	
	@MockBean
	private  TrackerService trackerService;
	
	@MockBean
	private  UserService userService;
	
	@Test
	@DisplayName("/home/notes unauthorized access test.")
	void homeNotesUnauthorized() throws Exception {
		
		 mockMvc.perform(get("/home/notes"))
	      .andExpect(status().isUnauthorized());
	}
	@Test
	@WithMockUser
	@DisplayName("/home/notes authorized access test. Error page redirect.")
	void homeNotesAuthError() throws Exception {
	    Users user = new Users("user","123");
    	Optional<Users> opt = Optional.of(user);
    	
    	when(userService.findByUsername("user")).thenReturn(opt);
    	
   
    	when(trackerService.findByIdAndUserId(1L, null)).thenReturn(null);
		
    	mockMvc.perform(get("/home/notes")
    			.param("storyId", "1")
    			.param("trackerName","abc"))
    			.andExpect(status().isOk())
    			.andExpect(view().name("errorPage.html"))
    			.andExpect(model().attribute("errorMsg", "Error. Can't access other people data."));
	}
	@Test
	@WithMockUser
	@DisplayName("/home/notes authorized access test. Happy flow.")
	void homeNotesAuthHappyFlow() throws Exception {
	    Users user = new Users("user","123");
    	Optional<Users> opt = Optional.of(user);
    	
    	when(userService.findByUsername("user")).thenReturn(opt);
    	
    	Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
   
    	when(trackerService.findByIdAndUserId(1L, null)).thenReturn(b);
		
    	mockMvc.perform(get("/home/notes")
    			.param("storyId", "1")
    			.param("trackerName","abc"))
    			.andExpect(status().isOk())
    			.andExpect(view().name("notes.html"));
    			
	}
	@Test
	@DisplayName("/home/addNotes unauthorized access test.")
	void homeAddNotesUnauthorized() throws Exception {
		
		 mockMvc.perform(post("/home/addNotes").with(csrf()))
	      .andExpect(status().isUnauthorized());
	}
	@Test
	@WithMockUser("user")
	@DisplayName("/home/addNotes authorized. Negative flow. Error mapping.")
	void homeAddNotesAuthorizedError() throws Exception {
		
		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		mockMvc.perform(post("/home/addNotes").with(csrf())
				.param("name", "default")
				.param("comments", "test")
				.param("storyId", "1")
				.param("trackerName", "test"))
			.andExpect(status().isOk())
			.andExpect(view().name("errorPage.html"))
			.andExpect(model().attribute("errorMsg", "Error. Can't add notes for other people."));
	}
	@Test
	@WithMockUser("user")
	@DisplayName("/home/addNotes authorized. Happy flow.")
	void homeAddNotesAuthorizedHappyFlow() throws Exception {
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
   
    	when(trackerService.findByIdAndUserId(1L, null)).thenReturn(b);
    	
		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		mockMvc.perform(post("/home/addNotes").with(csrf())
				.param("name", "default")
				.param("comments", "test")
				.param("storyId", "1")
				.param("trackerName", "test"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/home/notes?storyId=1&trackerName=test"));
		
	}
	
	@Test
	@DisplayName("/deleteNote unauthorized access test.")
	void deleteNoteUnauthorized() throws Exception {
		
		 mockMvc.perform(post("/deleteNote").with(csrf()))
	      .andExpect(status().isUnauthorized());
	}
	
	
	@Test
	@WithMockUser("user")
	@DisplayName("/deleteNote authorized. Negative flow. Error page.")
	void deleteNoteAuthorizedError() throws Exception{
		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		mockMvc.perform(post("/deleteNote").with(csrf())
				.param("notesId", "1")
				.param("storyId", "1")
				.param("trackerName", "test"))
				.andExpect(status().isOk())
				.andExpect(view().name("errorPage.html"))
				.andExpect(model().attribute("errorMsg", "Error. Can't delete notes for other people."));

	}
	@Test
	@WithMockUser("user")
	@DisplayName("/deleteNote authorized. Happy flow.")
	void deleteNoteAuthorizedHappyFlow()  throws Exception{
		
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
		when(trackerService.findByIdAndUserId(1L, null)).thenReturn(b);
		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		
		mockMvc.perform(post("/deleteNote").with(csrf())
				.param("notesId", "1")
				.param("storyId", "1")
				.param("trackerName", "test"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/notes?storyId=1&trackerName=test"));
	}
	
	@Test
	@DisplayName("/home/editNote unauthorized test.")
	void homeEditNoteUnauthorizedTest() throws Exception{
		
		mockMvc.perform(post("/home/editNote").with(csrf())
						.param("notesId", "1"))
						.andExpect(status().isUnauthorized());
	}
	@Test
	@WithMockUser("user")
	@DisplayName("/home/editNote authorized test. Tracker is null. Id is null. ErrorPage.")
	void homeEditNoteauthorizedTestErrorPage() throws Exception{
		Notes note = new Notes("Default", "Test");
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
		note.setTracker(b);
		when(notesService.findById(1L)).thenReturn(note);

		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		when(notesService.findByIdAndTrackerId(1L, null)).thenReturn(null);
		mockMvc.perform(post("/home/editNote").with(csrf())
						.param("notesId", "1"))
						.andExpect(status().isOk())
						.andExpect(view().name("errorPage.html"))
						.andExpect(model().attribute("errorMsg","Error. Can't edit notes from other people."));
	}
	@Test
	@WithMockUser("user")
	@DisplayName("/home/editNote authorized test. TrackerId is fine. notesService.findByIdAndTrackerId is null so it will go to ErrorPage.")
	void homeEditNoteauthorizedTestErrorPageId() throws Exception{
		Notes note = new Notes("Default", "Test");
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
		note.setTracker(b);
		when(notesService.findById(1L)).thenReturn(note);

		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		when(trackerService.findByIdAndUserId(null, null)).thenReturn(b);
		when(notesService.findByIdAndTrackerId(1L, null)).thenReturn(null);
		mockMvc.perform(post("/home/editNote").with(csrf())
						.param("notesId", "1"))
						.andExpect(status().isOk())
						.andExpect(view().name("errorPage.html"))
						.andExpect(model().attribute("errorMsg","Error. Can't edit notes from other people."));
	}
	
	@Test
	@WithMockUser("user")
	@DisplayName("/home/editNote authorized test. Happy flow.")
	void homeEditNoteauthorizedTestHappyFlow() throws Exception{
		Notes note = new Notes("Default", "Test");
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
		note.setTracker(b);
		when(notesService.findById(1L)).thenReturn(note);

		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		when(trackerService.findByIdAndUserId(null, null)).thenReturn(b);
		when(notesService.findByIdAndTrackerId(1L, null)).thenReturn(note);
		mockMvc.perform(post("/home/editNote").with(csrf())
						.param("notesId", "1"))
						.andExpect(status().isOk())
						.andExpect(view().name("editNote.html"));
					
	}
	
	@Test
	@DisplayName("/home/updateNote unauthorized test.")
	void homeUpdateNoteUnauthorizedTest() throws Exception{
		
		mockMvc.perform(post("/home/updateNote").with(csrf())
						.param("notesName", "Default")
						.param("notesComment", "Good movie.")
						.param("notesId", "1")
						.param("storyId", "1")
						.param("trackerName", "Pulp Fiction"))
						.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser("user")
	@DisplayName("/home/updateNote authorized test. Tracker null. Error Page.")
	void homeUpdateNoteAuthorizedError() throws Exception{
		Notes note = new Notes("Default", "Test");
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
		note.setTracker(b);
		when(notesService.findById(1L)).thenReturn(note);

		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		when(trackerService.findByIdAndUserId(1L, null)).thenReturn(null); 
		when(notesService.findByIdAndTrackerId(1L, 1L)).thenReturn(note);
		
		mockMvc.perform(post("/home/updateNote").with(csrf())
						.param("notesName", "Default")
						.param("notesComment", "Good movie.")
						.param("notesId", "1")
						.param("storyId", "1")
						.param("trackerName", "Pulp Fiction")
						.param("createdOn", "2023-11-10T13:02:20")
						.param("lastModified","2023-11-10T13:02:20"))
						.andExpect(status().isOk())
						.andExpect(view().name("errorPage.html"))
						.andExpect(model().attribute("errorMsg","Error. Can't update notes from other people."));
	}
	@Test
	@WithMockUser("user")
	@DisplayName("/home/updateNote authorized test. Empty Notes. Empty Tracker.Error Page.")
	void homeUpdateNoteAuthorizedErrorEmptyNote() throws Exception{
		Notes note = new Notes("Default", "Test");
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
		note.setTracker(b);
		when(notesService.findById(1L)).thenReturn(note);

		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		when(trackerService.findByIdAndUserId(null, null)).thenReturn(null); // Happy flow. will return good tracker object.
		when(notesService.findByIdAndTrackerId(1L, null)).thenReturn(null);
		
		mockMvc.perform(post("/home/updateNote").with(csrf())
						.param("notesName", "Default")
						.param("notesComment", "Good movie.")
						.param("notesId", "1")
						.param("storyId", "1")
						.param("trackerName", "Pulp Fiction")
						.param("createdOn", "2023-11-10T13:02:20")
						.param("lastModified","2023-11-10T13:02:20"))
						.andExpect(status().isOk())
						.andExpect(view().name("errorPage.html"))
						.andExpect(model().attribute("errorMsg","Error. Can't update notes from other people."));
	}
	
	@Test
	@WithMockUser("user")
	@DisplayName("/home/updateNote authorized test.Happy flow.")
	void homeUpdateNoteAuthorizedHappyFlow() throws Exception{
		Notes note = new Notes("Default", "Test");
		Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
				"Finished2");
    	b.setCreatedOn(LocalDateTime.now());
    	b.setLastModified(LocalDateTime.now());
		note.setTracker(b);
		when(notesService.findById(1L)).thenReturn(note);

		when(userService.findByUsername("user")).thenReturn(Optional.of(new Users("user","123")));
		when(trackerService.findByIdAndUserId(1L, null)).thenReturn(b); // Happy flow. will return good tracker object.
		when(notesService.findByIdAndTrackerId(1L, 1L)).thenReturn(note); // happy flow
		
		mockMvc.perform(post("/home/updateNote").with(csrf())
						.param("notesName", "Default")
						.param("notesComment", "Good movie.")
						.param("notesId", "1")
						.param("storyId", "1")
						.param("trackerName", "Pulp Fiction")
						.param("createdOn", "2023-11-10T13:02:20")
						.param("lastModified","2023-11-10T13:02:20"))
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/home/notes?storyId=1&trackerName=Pulp+Fiction"));
	}
}
