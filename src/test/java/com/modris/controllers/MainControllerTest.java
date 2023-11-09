package com.modris.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.modris.Main;
import com.modris.configurations.ProjectConfig;
import com.modris.controller.MainController;
import com.modris.model.Categories;
import com.modris.model.Status;
import com.modris.model.Tracker;
import com.modris.model.Users;
import com.modris.security.JpaUserDetailsService;
import com.modris.services.CategoriesService;
import com.modris.services.StatusService;
import com.modris.services.TrackerService;
import com.modris.services.UserService;

@ContextConfiguration(classes={Main.class,ProjectConfig.class})
@WebMvcTest(MainController.class)
@DisplayName("MainController mapping tests:")
public class MainControllerTest {
	
		@Autowired
	    private MockMvc mockMvc;
		
		@MockBean
		JpaUserDetailsService jpaUserDetailsService;
		   
	    @MockBean
	    private TrackerService trackerService;

	    @MockBean
	    private CategoriesService categoriesService;

	    @MockBean
	    private StatusService statusService;
	    
	    @MockBean
	    private UserService userService;
	  
	    @Test
		@DisplayName("@GetMapping(/) test. Not logged in.")
		public void getMappingSlashTest() throws Exception{
	    	
	        mockMvc.perform(get("/"))
	        	      .andExpect(status().isOk())
	        	      .andExpect(view().name("defaultPage.html"));
	        
	    }
	    @Test
		@DisplayName("@GetMapping(/home) test. Unauthorized access test before logged in.")
		public void getMappingHomeTestUnauthorized() throws Exception{
	    	
	        mockMvc.perform(get("/home"))
	        	      .andExpect(status().isUnauthorized());
	    }

	    @Test
	    @WithMockUser("user")
		@DisplayName("@GetMapping('/')test. Authorized users redirected to /home mapping.")
		public void getMappingSlashAuthorized() throws Exception{
	
	        mockMvc.perform(get("/"))
	        	      .andExpect(redirectedUrl("/home"));
	    }
	    
	    @Nested
	    class homePagePageNumTests {
	    	
	       	List<Tracker> tList = new ArrayList<>();
	       	String sortField = "id";
	       	String sortDir="asc";
	       	
	       	@BeforeEach
	    	void beforeEach() {
	    		Users user = new Users("user","123");
		    	Optional<Users> opt = Optional.of(user);
		    	
		    	when(userService.findByUsername("user")).thenReturn(opt);

		    	Tracker a = new Tracker("Pulp Fiction",new Categories("Movies"), new Status("Completed"),
		    						"Finished");
		    	
		    	a.setCreatedOn(LocalDateTime.now());
		    	a.setLastModified(LocalDateTime.now());
		    
		    	Tracker b = new Tracker("Pulp Fiction2",new Categories("Movies"), new Status("Completed"),
						"Finished2");
		    	b.setCreatedOn(LocalDateTime.now());
		    	b.setLastModified(LocalDateTime.now());
		   
		    	tList.add(a);
		    	tList.add(b);
		    	Page<Tracker> paged = new PageImpl<>(tList);
		    	//System.out.println(paged.getSize());
		    								//userId, pageNum,pageSize,sortField,sortDir
		    	when(trackerService.findAllPagedWithUserId(null, 1, 5, sortField, sortDir)).thenReturn(paged);
		
		    	assertEquals(null, user.getId(), 
		    			() -> "Mocked User should have id null because it's not from the real repository.");
		    	assertEquals(2, paged.getSize(), 
		    			() -> "List<Tracker> tList should have 2 tracker objects. PageImpl should convert to Page<Tracker> object.");
		    	
	    	}
	    	
		    	@Test
		    	@WithMockUser("user")
				@DisplayName("@GetMapping(/home) test. Authorized user can access.")
				public void getMappingHomeTestAuthorized() throws Exception{
		    		
		    
		    	mockMvc.perform(get("/home"))
		    					.andExpect(view().name("Welcome.html"));
			    }
	
			    @Test
				@DisplayName("@GetMapping(/home/page/{pageNum}) test. Unauthorized can't access.")
			    void homePagePageNumUnauthorizedTest() throws Exception {
	
			    	mockMvc.perform(get("/home/page/1?sortField=id&sortDir=asc"))
			    						.andExpect(status().isUnauthorized());
			    			        
			    }
			    
			    @Test
			    @WithMockUser("user")
				@DisplayName("@GetMapping(/home/page/{pageNum}) test. Authorized. ID Ascending.")
			    void homePagePageNumAUTHORIZEDTestIdAsc() throws Exception {
	
			    	
			    	 mockMvc.perform(get("/home/page/1?sortField=id&sortDir=asc"))
						.andExpect(status().isOk())
						.andExpect(view().name("Welcome.html"));
			    	 
			    assertEquals("Pulp Fiction",tList.get(0).getName(), ()-> "ID ascending. First value should be Tracker a with name Pulp Fiction.");
			    assertEquals(2, tList.size());
			    }
			    
			    @Test
			    @WithMockUser("user")
				@DisplayName("@GetMapping(/home/page/{pageNum}) test. Authorized. CreatedOn added.")
			    void homePagePageNumAUTHORIZEDTestCreatedOn() throws Exception {
	
			    	
			    	 mockMvc.perform(get("/home/page/1?sortField=id&sortDir=asc&createdOn=ok"))
						.andExpect(status().isOk())
						.andExpect(view().name("Welcome.html"));
			    }
		    //end of nested test class 	
		 }
	    
	    @Test
		@DisplayName("@PostMapping(/pageSizeReq) test. Unauthorized.")
	    void pageSizeReqUnauthorized() throws Exception {

	    	 mockMvc.perform(post("/pageSizeReq").with(csrf()))
				.andExpect(status().isUnauthorized());
	    }
	    
	    @Test
	    @WithMockUser("user")
		@DisplayName("@PostMapping(/pageSizeReq) test. Authorized.")
	    void pageSizeReqAuthorized() throws Exception {

	    	
	    	 mockMvc.perform(post("/pageSizeReq").with(csrf())
	    			 .param("sortField", "id")
	    			 .param("sortDir", "asc")
	    			 .param("currentPage", "1")
	    			 .param("pageSize", "5")) // default is 5. Not explicitly set in this mapping though. 
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/page/1?sortField=id&sortDir=asc&pageSize=5"));
	    }
	    @Test
	    @WithMockUser("user")
		@DisplayName("@PostMapping(/pageSizeReq) test. Authorized. CreatedOn added.")
	    void pageSizeReqAuthorizedCreatedOnAdded() throws Exception {

	    	
	    	 mockMvc.perform(post("/pageSizeReq").with(csrf())
	    			 .param("sortField", "id")
	    			 .param("sortDir", "asc")
	    			 .param("currentPage", "1")
	    			 .param("pageSize", "5") // default is 5. Not explicitly set in this mapping though. 
	    			 .param("createdOn", "on")) 
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/page/1?sortField=id&sortDir=asc&createdOn=on&pageSize=5"));
	    }
	    
	    
	    @Test
	 	@DisplayName("@PostMapping(/show) test. Unauthorized.")
	 	void showUnauthorized() throws Exception {

	 	    	 mockMvc.perform(post("/show").with(csrf()))
	 				.andExpect(status().isUnauthorized());
	 	    }
	    
	    
	    @Test
	    @WithMockUser("user")
		@DisplayName("@PostMapping(/show) test. Authorized.")
	    void showTestAuthorized() throws Exception {

	    	
	    	 mockMvc.perform(post("/show").with(csrf())
	    			 .param("sortField", "name")
	    			 .param("sortDir", "asc")
	    			 .param("currentPage", "1")
	    			 .param("pageSize", "5")) // default is 5. Not explicitly set in this mapping though. 
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/page/1?sortField=name&sortDir=asc&pageSize=5"));
	    }
	    @Test
	    @WithMockUser("user")
		@DisplayName("@PostMapping(/show) test. Authorized. CreatedOn added")
	    void showTestAuthorizedCreatedOnAdded() throws Exception {

	    	
	    	 mockMvc.perform(post("/show").with(csrf())
	    			 .param("sortField", "name")
	    			 .param("sortDir", "asc")
	    			 .param("currentPage", "1")
	    			 .param("pageSize", "5")// default is 5. Not explicitly set in this mapping though. 
	    			 .param("createdOn", "on")) 
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/page/1?sortField=name&sortDir=asc&createdOn=on&pageSize=5"));
	    }
	    
	   
	    //-----------------------------------
	 
	 @Test
	 @DisplayName("@PostMapping(/addTracker) test. Unauthorized.")
	 void addTrackerUnauthorized() throws Exception {
	    	 mockMvc.perform(post("/addTracker").with(csrf()))
	 			.andExpect(status().isUnauthorized());
	     }
	
	@Test
	@WithMockUser
	@DisplayName("@PostMapping(/addTracker) receives the Tracker class and we can add it to the service successfully.")
	public void mainControllerReceivesTrackerClassHappyFlow() throws Exception{
	
		Categories category = new Categories("Movies");

	    Status status = new Status("Completed");

	    Tracker tracker = new Tracker("Pulp Fiction",
	    		category, status,"Some progress");
	    
	    Users user = new Users("user","123");
    	Optional<Users> opt = Optional.of(user);
    	
    	when(userService.findByUsername("user")).thenReturn(opt);

	    mockMvc.perform(post("/addTracker").with(csrf())
				    .param("name", "Pulp Fiction")
		            .param("category","Movies") 
		            .param("status","Completed") 
		            .param("progress", "Some progress")
		            .param("pageSize", "5")
		            .param("sortField", "id")
	    			.param("sortDir", "asc")
	    			.param("currentPage", "1"))
				   .andExpect(status().is3xxRedirection())
				   .andExpect(redirectedUrl("/home/page/1?sortField=id&sortDir=asc&pageSize=5"));
		
		verify(trackerService).addTracker(tracker);
		
	}

	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping(/addTracker) Happy flow 2. Data binding to Tracker object works. We test if we add a real Tracker object to TrackerService and then assertEquals")
	void mainControllerHappyFlow2() throws Exception {
		Categories category = new Categories("Books");
		Status status = new Status("Ongoing");
		
		ArgumentCaptor<Tracker> trackerCaptor = ArgumentCaptor.forClass(Tracker.class);
		
		Users user = new Users("user","123");
	    Optional<Users> opt = Optional.of(user);
	    	
	    when(userService.findByUsername("user")).thenReturn(opt);

		mockMvc.perform(post("/addTracker").with(csrf())
				.param("name", "A Song Of Fire And Ice")
				.param("category", "Books")
				.param("status", "Ongoing")
				.param("progress", "Hiatus")
				.param("pageSize", "5")
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/page/1?sortField=id&sortDir=asc&pageSize=5"));
				
		verify(trackerService).addTracker(trackerCaptor.capture());
				
		Tracker capturedTracker = trackerCaptor.getValue();
		assertEquals(null,capturedTracker.getId());
		assertEquals("A Song Of Fire And Ice",capturedTracker.getName());
		assertEquals("Books", capturedTracker.getCategory().getName());
		assertEquals("Ongoing",capturedTracker.getStatus().getName());
		assertEquals("Hiatus", capturedTracker.getProgress());
		
	}
	
	 @Test
	 @DisplayName("@PostMapping(/edit) test. Unauthorized.")
	 void editUnauthorized() throws Exception {
	    	 mockMvc.perform(post("/edit").with(csrf()))
	 			.andExpect(status().isUnauthorized());
	     }
	 
	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping /edit. Happy flow. ")
	void editMappingTestHappyFlow() throws Exception {
		Users user = new Users("user","123");
	    Optional<Users> opt = Optional.of(user);
	    	
	    when(userService.findByUsername("user")).thenReturn(opt);

	    
		Categories c = new Categories("Movies");
		Status s = new Status("Completed");
		Tracker t = new Tracker("Oppenheimer",c,s,"Watched");
		
		Long editId = 11L;

		List<Status> statusList = new ArrayList<>();
		List<Categories> categoriesList = new ArrayList<>();
		
	    when(trackerService.findByIdAndUserId(editId, null)).thenReturn(t);
	    when(categoriesService.findAll()).thenReturn(categoriesList);
		when(statusService.findAll()).thenReturn(statusList);

		mockMvc.perform(post("/edit").with(csrf())
				.param("editId", String.valueOf(editId))
				.param("pageSize", "5")
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))
				.andExpect(model().attribute("tracker", equalTo(t)))
				.andExpect(model().attribute("categoriesList",equalTo(categoriesList)))
				.andExpect(model().attribute("statusList",equalTo(statusList)))
				.andExpect(status().isOk())
				.andExpect(view().name("editStory.html"));	
	}
	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping /edit. Negative flow. Return errorPage.html ")
	void editMappingTestNegativeFlow() throws Exception {
		Users user = new Users("user","123");
	    Optional<Users> opt = Optional.of(user);
	    	
	    when(userService.findByUsername("user")).thenReturn(opt);
	    when(trackerService.findByIdAndUserId(1L, null)).thenReturn(null);

		mockMvc.perform(post("/edit").with(csrf())
				.param("editId", "1")
				.param("pageSize", "5")
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))
				.andExpect(view().name("errorPage.html"));	
	}
	
	
	 @Test
	 @DisplayName("@PostMapping(/editSaved) test. Unauthorized.")
	 void editSavedUnauthorized() throws Exception {
	    	 mockMvc.perform(post("/editSaved").with(csrf()))
	 			.andExpect(status().isUnauthorized());
	     }
	 
	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping /editSaved happy flow. trackerService.editSaved works and is called.")
	void editSavedMappingTest1() throws Exception {
		
		Users user = new Users("user","123");
	    Optional<Users> opt = Optional.of(user);
	    
		Categories c = new Categories("Movies");
		Status s = new Status("Completed");
		Tracker t = new Tracker("Oppenheimer",c,s,"Watched");
		Long id = 10L;
		t.setCreatedOn(LocalDateTime.now());
		
	    when(userService.findByUsername("user")).thenReturn(opt);
		when(trackerService.findByIdAndUserId(id, null)).thenReturn(t);
		    
		mockMvc.perform(post("/editSaved").with(csrf())
				.param("name", t.getName())
				.param("category",t.getCategory().getName())
				.param("status",t.getStatus().getName())
				.param("progress", t.getProgress())
				.param("createdOn",String.valueOf(t.getCreatedOn()))
				.param("id",String.valueOf(id))
				.param("pageSize", "5")
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))	
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/page/1?sortField=id&sortDir=asc&pageSize=5"));
		
		verify(trackerService).editSavedHibernate(t, id, null);
	
	}
	
	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping /editSaved negative flow.  ErrorPage called.")
	void editSavedMappingNegativeFlow() throws Exception {
		
		Users user = new Users("user","123");
	    Optional<Users> opt = Optional.of(user);
	    
		Categories c = new Categories("Movies");
		Status s = new Status("Completed");
		Tracker t = new Tracker("Oppenheimer",c,s,"Watched");
		Long id = 10L;
		t.setCreatedOn(LocalDateTime.now());
		
	    when(userService.findByUsername("user")).thenReturn(opt);
		when(trackerService.findByIdAndUserId(1L, null)).thenReturn(t);
		    
		mockMvc.perform(post("/editSaved").with(csrf())
				.param("name", t.getName())
				.param("category",t.getCategory().getName())
				.param("status",t.getStatus().getName())
				.param("progress", t.getProgress())
				.param("createdOn",String.valueOf(t.getCreatedOn()))
				.param("id",String.valueOf(id))
				.param("pageSize", "5")
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))	
				.andExpect(status().isOk())
				.andExpect(view().name("errorPage.html"));

	
	}
	 @Test
	 @DisplayName("@PostMapping(/delete) test. Unauthorized.")
	 void deleteUnauthorized() throws Exception {
	    	 mockMvc.perform(post("/delete").with(csrf()))
	 			.andExpect(status().isUnauthorized());
	     }
	 
	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping /delete negative flow.  ErrorPage called. id is null.")
	void deleteMappingNegativeFlow() throws Exception {
		
   
		mockMvc.perform(post("/delete").with(csrf())
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))	
				.andExpect(status().isOk())
				.andExpect(model().attribute("errorMsg","Error. Id can't be null."))
				.andExpect(view().name("errorPage.html"));

	}
	
	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping /delete negative flow.  ErrorPage called. Can't edit other people data.")
	void deleteMappingNegativeFlow2() throws Exception {
		
		Users user = new Users("user","123");
	    Optional<Users> opt = Optional.of(user);
	    
		Categories c = new Categories("Movies");
		Status s = new Status("Completed");
		Tracker t = new Tracker("Oppenheimer",c,s,"Watched");
		Long id = 10L;
		t.setCreatedOn(LocalDateTime.now());
		
	    when(userService.findByUsername("user")).thenReturn(opt);
		when(trackerService.findByIdAndUserId(1L, null)).thenReturn(t);
		    
		mockMvc.perform(post("/delete").with(csrf())
				.param("name", t.getName())
				.param("category",t.getCategory().getName())
				.param("status",t.getStatus().getName())
				.param("progress", t.getProgress())
				.param("createdOn",String.valueOf(t.getCreatedOn()))
				.param("idDelete",String.valueOf(id))
				.param("pageSize", "5")
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))	
				.andExpect(status().isOk())
				.andExpect(model().attribute("errorMsg","Error. Can't delete other people data."))
				.andExpect(view().name("errorPage.html"));

	}
	
	@Test
	@WithMockUser("user")
	@DisplayName("@PostMapping /delete happy flow.")
	void deleteMappingHappyFlow() throws Exception {
		
		Users user = new Users("user","123");
	    Optional<Users> opt = Optional.of(user);
	    
		Categories c = new Categories("Movies");
		Status s = new Status("Completed");
		Tracker t = new Tracker("Oppenheimer",c,s,"Watched");
		Long id = 10L;
		t.setCreatedOn(LocalDateTime.now());
		
	    when(userService.findByUsername("user")).thenReturn(opt);
		when(trackerService.findByIdAndUserId(id, null)).thenReturn(t);
		    
		mockMvc.perform(post("/delete").with(csrf())
				.param("name", t.getName())
				.param("category",t.getCategory().getName())
				.param("status",t.getStatus().getName())
				.param("progress", t.getProgress())
				.param("createdOn",String.valueOf(t.getCreatedOn()))
				.param("idDelete",String.valueOf(id))
				.param("pageSize", "5")
		        .param("sortField", "id")
	    		.param("sortDir", "asc")
	    		.param("currentPage", "1"))	
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/page/1?sortField=id&sortDir=asc&pageSize=5"));

		//verify(trackerService).deleteByIdAndUserId(id,null);
	}
	
	/*
	
	@Test
	@DisplayName("@PostMapping /delete happy flow test. Verify everything is OK and testService.deleteById is called.")
	void mainControllerDeleteMappingTest() throws Exception {
		
		Long idToDelete = 5L;
		
		mockMvc.perform(post("/delete")
				.param("idDelete",String.valueOf(idToDelete)))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
		
		verify(trackerService).deleteById(idToDelete);
		
	}

	
	//editSaved. 1) Receives Tracker object 2) Receives ID 3) redirect 4) verify 
	
	

	*/
	
	
}
