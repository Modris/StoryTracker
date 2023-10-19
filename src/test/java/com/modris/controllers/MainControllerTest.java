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
	@DisplayName("MainController @PostMapping(/addTracker) receives the Tracker class and we can add it to the service successfully.")
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
	@DisplayName("MainController @PostMapping(/addTracker) Happy flow 2. Data binding to Tracker object works. We test if we add a real Tracker object to TrackerService and then assertEquals")
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
		//System.out.println(capturedTracker.toString());
		assertEquals(null,capturedTracker.getId());
		assertEquals("A Song Of Fire And Ice",capturedTracker.getName());
		assertEquals("Books", capturedTracker.getCategory().getName());
		assertEquals("Ongoing",capturedTracker.getStatus().getName());
		assertEquals("Hiatus", capturedTracker.getProgress());
		
	}
	
	@Test
	@DisplayName("MainController @GetMapping(/) Happy Flow test. We call / sucessfully and model.addAttribute works. ")
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
	
	
	
	
}
