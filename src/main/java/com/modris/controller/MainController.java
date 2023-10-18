package com.modris.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.modris.model.Categories;
import com.modris.model.Status;
import com.modris.model.Tracker;
import com.modris.services.CategoriesService;
import com.modris.services.StatusService;
import com.modris.services.TrackerService;

@Controller
public class MainController {

	private final TrackerService trackerService;
	private final CategoriesService categoriesService;
	private final StatusService statusService;
	@Autowired
	public MainController(TrackerService trackerService, 
			CategoriesService categoriesService,
			StatusService statusService) {
		this.trackerService = trackerService;
		this.categoriesService = categoriesService;
		this.statusService = statusService;
	}
	
	@GetMapping("/")
	public String mainPage(Model model) {
		List<Categories> categoriesList = categoriesService.findAll();
		List<Status> statusList = statusService.findAll();
		model.addAttribute("categoriesList", categoriesList);
		model.addAttribute("statusList",statusList);
		return "Welcome.html";
	}

	@PostMapping("/addTracker")
	public String addTracker(Tracker t, Model model) {

		trackerService.addTracker(t);
		return "redirect:/";
	}
	
}
