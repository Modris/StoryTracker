package com.modris.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String mainPage(@RequestParam(value = "createdOn",required=false) String createdOn,
			@RequestParam(value = "lastModified",required=false) String lastModified,
			@RequestParam(value = "lastRead",required=false) String lastRead,
			Model model) {
		
		List<Categories> categoriesList = categoriesService.findAll();
		List<Status> statusList = statusService.findAll();
		List<Tracker> trackerList = trackerService.findAll();

		model.addAttribute("createdOnAnswer",createdOn);
		model.addAttribute("lastModifiedAnswer",lastModified);
		model.addAttribute("lastReadAnswer",lastRead);
		model.addAttribute("categoriesList", categoriesList);
		model.addAttribute("statusList",statusList);
		model.addAttribute("trackerList",trackerList);
		return "Welcome.html";
	}
	@GetMapping("/show")
	public String showHideColumns(@RequestParam(value = "createdOn",required=false) String createdOn,
								@RequestParam(value = "lastModified",required=false) String lastModified,
								@RequestParam(value = "lastRead",required=false) String lastRead,
								Model model,
								RedirectAttributes redirectAttributes){

		redirectAttributes.addAttribute("createdOn",createdOn);
		redirectAttributes.addAttribute("lastModified",lastModified);
		redirectAttributes.addAttribute("lastRead",lastRead);
		return "redirect:/";
	}
	/*
	<input type="checkbox" name="createdOn"/>
	<input type="checkbox" name="LastModified"/>
	<input type="checkbox" name="LastRead"/
	*/
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
		return "editStory.html";
	}
	
	@PostMapping("editSaved")
	public String editSaved(Tracker t, @RequestParam("id") Long id) {
		trackerService.editSavedHibernate(t,id);
		/*trackerService.editSaved(t.getName(),
				t.getCategory().getId(),
				t.getStatus().getId(),
				t.getCreatedOn(),
				t.getProgress(),
				id);
				*/
		return "redirect:/";
	}
	
	//------------------
	@PostMapping("/delete")
	public String deleteWhereId(@RequestParam(required=false,name="idDelete") Long id) {
		trackerService.deleteById(id);
		return "redirect:/";
	}

	
}
