package com.modris.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		List<Tracker> trackerList = trackerService.findAll();
		model.addAttribute("categoriesList", categoriesList);
		model.addAttribute("statusList",statusList);
		model.addAttribute("trackerList",trackerList);
		return "Welcome.html";
	}

	@PostMapping("/addTracker")
	public String addTracker(Tracker t) {
		trackerService.addTracker(t);
		return "redirect:/";
	}
	
	//EDIT PAGE 
	@PostMapping("/edit")
	public String editStory(@RequestParam(name="editId") Long id , Model model) {
		Tracker t = trackerService.findById(id);
		List<Status> statusList = statusService.findAll();
		List<Categories> categoriesList = categoriesService.findAll();
		model.addAttribute("categoriesList", categoriesList);
		model.addAttribute("statusList",statusList);
		model.addAttribute("tracker", t);
		return "editPage.html";
	}
	
	@PostMapping("editSaved")
	public String editSaved(Tracker t, @RequestParam("id") Long id) {
		trackerService.editSaved(t.getName(),
				t.getCategory().getId(),
				t.getStatus().getId(),
				t.getCreatedOn(),
				t.getProgress(),
				id);
		return "redirect:/";
	}
	
	//------------------
	@PostMapping("/delete")
	public String deleteWhereId(@RequestParam(required=false,name="idDelete") Long id) {
		trackerService.deleteById(id);
		return "redirect:/";
	}

	
}
