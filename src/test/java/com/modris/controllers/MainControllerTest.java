package com.modris.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.modris.controller.MainController;
import com.modris.model.Categories;
import com.modris.model.Status;
import com.modris.model.Tracker;
import com.modris.services.CategoriesService;
import com.modris.services.StatusService;
import com.modris.services.TrackerService;

@WebMvcTest(MainController.class)
@DisplayName("MainController mapping tests:")
public class MainControllerTest {

		@Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private TrackerService trackerService;

	    @MockBean
	    private CategoriesService categoriesService;

	    @MockBean
	    private StatusService statusService;

	@Test
	@DisplayName("@PostMapping(/addTracker) receives the Tracker class and we can add it to the service successfully.")
	public void mainControllerReceivesTrackerClassHappyFlow() throws Exception{
	
		Categories category = new Categories("Movies");

	    Status status = new Status("Completed");

	    Tracker tracker = new Tracker("Pulp Fiction",
	    		category, status,"Some progress");


	    mockMvc.perform(post("/addTracker")
				    .param("name", "Pulp Fiction")
		            .param("category","Movies") 
		            .param("status","Completed") 
		            .param("progress", "Some progress"))
				   .andExpect(status().is3xxRedirection())
				   .andExpect(redirectedUrl("/"));
		
		verify(trackerService).addTracker(tracker);
		// Real spring boot data:
		//Tracker [id=null, name=Random Name, category=Categories [name=Books], status=Status [name=Ongoing], progress=Page 47, createdOn=null, lastRead=null
	}
	@Test
	@DisplayName("@PostMapping(/addTracker) Happy flow 2. Data binding to Tracker object works. We test if we add a real Tracker object to TrackerService and then assertEquals")
	void mainControllerHappyFlow2() throws Exception {
		Categories category = new Categories("Books");
		Status status = new Status("Ongoing");
		
		ArgumentCaptor<Tracker> trackerCaptor = ArgumentCaptor.forClass(Tracker.class);
		
		mockMvc.perform(post("/addTracker")
				.param("name", "A Song Of Fire And Ice")
				.param("category", "Books")
				.param("status", "Ongoing")
				.param("progress", "Hiatus"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
				
		verify(trackerService).addTracker(trackerCaptor.capture());
				
		Tracker capturedTracker = trackerCaptor.getValue();
		assertEquals(null,capturedTracker.getId());
		assertEquals("A Song Of Fire And Ice",capturedTracker.getName());
		assertEquals("Books", capturedTracker.getCategory().getName());
		assertEquals("Ongoing",capturedTracker.getStatus().getName());
		assertEquals("Hiatus", capturedTracker.getProgress());
		
	}
	
	@Test
	@DisplayName("@GetMapping(/) Happy Flow test. We call / sucessfully and model.addAttribute works. ")
	public void mainControllerDefaultPageHappyFlowTest() throws Exception{
		List<Categories> cList = new ArrayList<>();
		List<Status> sList = new ArrayList<>();
		List<Tracker> tList = new ArrayList<>();
		
		when(categoriesService.findAll()).thenReturn(cList);
		when(statusService.findAll()).thenReturn(sList);
		when(trackerService.findAll()).thenReturn(tList);
		
		mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("Welcome.html"))
        .andExpect(model().attribute("categoriesList", equalTo(cList)))
        .andExpect(model().attribute("statusList", equalTo(sList)))
        .andExpect(model().attribute("trackerList", equalTo(tList)));

	}
	
	//post edit
	//post editSaved
	//post delete
	
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
	
	@Test
	@DisplayName("@PostMapping /editSaved happy flow. trackerService.editSaved works and is called.")
	void editSavedMappingTest1() throws Exception {
		//MainTest problem: Isolation. 
		//I can't get categories,status ID because there are no setters. It's auto_increment on database.
		
		Categories c = new Categories("Movies");
		Status s = new Status("Completed");
		Tracker t = new Tracker("Oppenheimer",c,s,"Watched");
		Long id = 10L;
		t.setCreatedOn(LocalDateTime.now());
		

		mockMvc.perform(post("/editSaved")
				.param("name", t.getName())
				.param("category",t.getCategory().getName())
				.param("status",t.getStatus().getName())
				.param("progress", t.getProgress())
				.param("createdOn",String.valueOf(t.getCreatedOn()))
				.param("id",String.valueOf(id)))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));
		
		verify(trackerService).editSaved(
				t.getName(),
				t.getCategory().getId(),
				t.getStatus().getId(),
				t.getCreatedOn(),
				t.getProgress(),
				id);
	}
	@Test
	@DisplayName("@PostMapping /edit. Happy flow. ")
	void editSavedMappingTest2() throws Exception {
		Categories c = new Categories("Movies");
		Status s = new Status("Completed");
		Tracker t = new Tracker("Oppenheimer",c,s,"Watched");
		
		Long editId = 11L;

		List<Status> statusList = new ArrayList<>();
		List<Categories> categoriesList = new ArrayList<>();
		
	    when(trackerService.findById(editId)).thenReturn(t);
	    when(categoriesService.findAll()).thenReturn(categoriesList);
		when(statusService.findAll()).thenReturn(statusList);

		mockMvc.perform(post("/edit")
				.param("editId", String.valueOf(editId)))
				.andExpect(model().attribute("tracker", equalTo(t)))
				.andExpect(model().attribute("categoriesList",equalTo(categoriesList)))
				.andExpect(model().attribute("statusList",equalTo(statusList)))
				.andExpect(status().isOk())
				.andExpect(view().name("editPage.html"));	
	}
	
	
	
}
