package com.modris.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
			@RequestParam(value="pageNum", required=false) String pageNum,
			Model model,
		 RedirectAttributes redirectAttributes) {

		if(pageNum == null) {
			pageNum="1";
		}
		return viewPage(Integer.valueOf(pageNum),createdOn, lastModified, lastRead, "id", "asc",model);
	}
	
	@GetMapping("/page/{pageNum}")
	public String viewPage(@PathVariable(name = "pageNum") int pageNum,
			@RequestParam(value = "createdOn",required=false) String createdOnAnswer,
			@RequestParam(value = "lastModified",required=false) String lastModifiedAnswer,
			@RequestParam(value = "lastRead",required=false) String lastReadAnswer,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			Model model) {
		
	 	List<Categories> categoriesList = categoriesService.findAll();
		List<Status> statusList = statusService.findAll();
		
		 
	    model.addAttribute("sortField", sortField);
	    model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("createdOnAnswer",createdOnAnswer);
		model.addAttribute("lastModifiedAnswer",lastModifiedAnswer);
		model.addAttribute("lastReadAnswer",lastReadAnswer);
		model.addAttribute("categoriesList", categoriesList);
		model.addAttribute("statusList",statusList);
	
		
		Page<Tracker> paged = trackerService.findAllPaged(pageNum, sortField,sortDir);
		
		List<Tracker> trackerListPaged = paged.getContent();
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", paged.getTotalPages());
		model.addAttribute("totalItems", paged.getTotalElements());
		model.addAttribute("trackerListPaged",trackerListPaged);
		
		
		return "test.html";
	}
	
	@GetMapping("/page/show")
	public String showHideColumns(@RequestParam(value = "createdOn",required=false) String createdOn,
								@RequestParam(value = "lastModified",required=false) String lastModified,
								@RequestParam(value = "lastRead",required=false) String lastRead,
								@RequestParam(value="currentPage") String currentPage,
								Model model,
								RedirectAttributes redirectAttributes){
		redirectAttributes.addAttribute("pageNum", currentPage);
		redirectAttributes.addAttribute("createdOn",createdOn);
		redirectAttributes.addAttribute("lastModified",lastModified);
		redirectAttributes.addAttribute("lastRead",lastRead);
		return "redirect:/";
		//return viewPage(Integer.valueOf(currentPage),createdOn, lastModified, lastRead, model);
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
