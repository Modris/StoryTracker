package com.modris.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
import com.modris.model.Users;
import com.modris.services.CategoriesService;
import com.modris.services.StatusService;
import com.modris.services.TrackerService;
import com.modris.services.UserService;

@Controller
public class MainController {

	private final TrackerService trackerService;
	private final CategoriesService categoriesService;
	private final StatusService statusService;
	private final UserService userService;
	@Autowired
	public MainController(TrackerService trackerService, CategoriesService categoriesService,
			StatusService statusService, UserService userService) {
		this.trackerService = trackerService;
		this.categoriesService = categoriesService;
		this.statusService = statusService;
		this.userService = userService;
	}

	@GetMapping("/")
	public String mainPage(@RequestParam(value = "createdOn",required=false) String createdOn,
			@RequestParam(value = "lastModified",required=false) String lastModified,
			@RequestParam(value = "lastRead",required=false) String lastRead,
			@RequestParam(value = "lastReadDays",required=false) String lastReadDays,
			@RequestParam(value="pageNum", required=false) String pageNum,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			Model model,
		 RedirectAttributes redirectAttributes) {
		
		if(pageNum == null) {
			pageNum="1";
		}
		if(pageSize == null) {
			pageSize = "5";
		}

		return viewPage(Integer.valueOf(pageNum),createdOn, lastModified, lastRead, 
				lastReadDays,pageSize, "id", "asc",model);
	}
	
	@GetMapping("/page/{pageNum}")
	public String viewPage(@PathVariable(name = "pageNum") int pageNum,
			@RequestParam(value = "createdOn",required=false) String createdOnAnswer,
			@RequestParam(value = "lastModified",required=false) String lastModifiedAnswer,
			@RequestParam(value = "lastRead",required=false) String lastReadAnswer,
			@RequestParam(value = "lastReadDays",required=false) String lastReadDaysAnswer,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			Model model) {
	
		
	 	List<Categories> categoriesList = categoriesService.findAll();
		List<Status> statusList = statusService.findAll();
		
		if(pageSize == null) {
			pageSize = "5";
		}

		 model.addAttribute("pageSize", pageSize);
	    model.addAttribute("sortField", sortField);
	    model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("createdOnAnswer",createdOnAnswer);
		model.addAttribute("lastModifiedAnswer",lastModifiedAnswer);
		model.addAttribute("lastReadAnswer",lastReadAnswer);
		model.addAttribute("lastReadDaysAnswer",lastReadDaysAnswer);
		model.addAttribute("categoriesList", categoriesList);
		model.addAttribute("statusList",statusList);
	
		
		Page<Tracker> paged = trackerService.findAllPaged(pageNum, Integer.valueOf(pageSize), sortField,sortDir);
		
		List<Tracker> trackerListPaged = paged.getContent();
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", paged.getTotalPages());
		model.addAttribute("totalItems", paged.getTotalElements());
		model.addAttribute("trackerListPaged",trackerListPaged);
		
		
		return "Welcome.html";
	}
	@PostMapping("/pageSizeReq")
	public String pageSizeReq(@RequestParam(value = "createdOn",required=false) String createdOn,
			@RequestParam(value = "lastModified",required=false) String lastModified,
			@RequestParam(value = "lastRead",required=false) String lastRead,
			@RequestParam(value = "lastReadDays",required=false) String lastReadDays,
			@RequestParam(value="currentPage") String currentPage,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			Model model,
			RedirectAttributes redirectAttributes){


			return pagedUrl(currentPage,sortField,sortDir,pageSize, createdOn,lastModified,lastRead,lastReadDays);
	
	}
	@PostMapping("/show")
	public String showHideColumns(@RequestParam(value = "createdOn",required=false) String createdOn,
								@RequestParam(value = "lastModified",required=false) String lastModified,
								@RequestParam(value = "lastRead",required=false) String lastRead,
								@RequestParam(value = "lastReadDays",required=false) String lastReadDays,
								@RequestParam(value="currentPage") String currentPage,
								@Param("sortField") String sortField,
								@Param("sortDir") String sortDir,
								@RequestParam(value = "pageSize",required=false) String pageSize,
								Model model,
								RedirectAttributes redirectAttributes){
		
		//redirectAttributes.addAttribute("pageNum", currentPage);
		/*redirectAttributes.addAttribute("createdOn",createdOn);
		redirectAttributes.addAttribute("lastModified",lastModified);
		redirectAttributes.addAttribute("lastRead",lastRead);
		redirectAttributes.addAttribute("lastReadDays",lastReadDays);
		return "redirect:/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
		*/
		return pagedUrl(currentPage,sortField,sortDir,pageSize, createdOn,lastModified,lastRead,lastReadDays);
		
			
	}

	@PostMapping("/addTracker")
	public String addTracker(Tracker t,
			@RequestParam(value = "createdOnNoTrackerBind",required=false) String createdOn,
			@RequestParam(value = "lastModifiedNoTrackerBind",required=false) String lastModified,
			@RequestParam(value = "lastReadNoTrackerBind",required=false) String lastRead,
			@RequestParam(value = "lastReadDays",required=false) String lastReadDays,
			@RequestParam(value="currentPage") String currentPage,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			Principal principal) {
		
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		t.setUserId(userExtracted.getId());
		
		trackerService.addTracker(t);
		
		
		return pagedUrl(currentPage,sortField,sortDir,pageSize, createdOn,lastModified,lastRead,lastReadDays);
	
	}
	
	//EDIT PAGE 
	@PostMapping("/edit")
	public String editStory(@RequestParam(name="editId") Long id , 
			@RequestParam(value = "createdOn",required=false) String createdOn,
			@RequestParam(value = "lastModified",required=false) String lastModified,
			@RequestParam(value = "lastRead",required=false) String lastRead,
			@RequestParam(value = "lastReadDays",required=false) String lastReadDays,
			@RequestParam(value="currentPage") String currentPage,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			Model model) {
		Tracker t = trackerService.findById(id);
		List<Status> statusList = statusService.findAll();
		List<Categories> categoriesList = categoriesService.findAll();
		
		model.addAttribute("createdOnAnswer", createdOn);
		model.addAttribute("lastModifiedAnswer", lastModified);
		model.addAttribute("lastReadAnswer", lastRead);
		model.addAttribute("lastReadDaysAnswer", lastReadDays);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
	
		model.addAttribute("categoriesList", categoriesList);
		model.addAttribute("statusList",statusList);
		model.addAttribute("tracker", t);
		model.addAttribute("pageSize", pageSize);
		return "editStory.html";
	}
	
	@PostMapping("editSaved")
	public String editSaved(Tracker t, @RequestParam("id") Long id,
			@RequestParam(value = "createdOnNoTrackerBind",required=false) String createdOn,
			@RequestParam(value = "lastModifiedNoTrackerBind",required=false) String lastModified,
			@RequestParam(value = "lastReadNoTrackerBind",required=false) String lastRead,
			@RequestParam(value = "lastReadDaysNoTrackerBind",required=false) String lastReadDays,
			@RequestParam(value="currentPage") String currentPage,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir)
			
			 {
		trackerService.editSavedHibernate(t,id);
		
		return pagedUrl(currentPage,sortField,sortDir,pageSize, createdOn,lastModified,lastRead,lastReadDays);
	}
	
	//------------------
	@PostMapping("/delete")
	public String deleteWhereId(
			@RequestParam(required=false,name="idDelete") Long id,
			@RequestParam(value = "createdOn",required=false) String createdOn,
			@RequestParam(value = "lastModified",required=false) String lastModified,
			@RequestParam(value = "lastRead",required=false) String lastRead,
			@RequestParam(value = "lastReadDays",required=false) String lastReadDays,
			@RequestParam(value="currentPage") String currentPage,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir)
										{
		trackerService.deleteById(id);
		
		return pagedUrl(currentPage,sortField,sortDir, pageSize, createdOn,lastModified,lastRead,lastReadDays);
		
	}

	private String pagedUrl(String currentPage,String sortField, String sortDir, 
			String pageSize,String createdOn,  String lastModified,String lastRead, 
			String lastReadDays) {
	
		
		String url = "redirect:/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
		if(createdOn !=null && (createdOn.equals("true") || createdOn.equals("on"))) {
			url+="&createdOn=on";
		}
		
		url+="&pageSize="+pageSize;
		
		
		if(lastModified !=null && (lastModified.equals("true") || lastModified.equals("on"))) {
			url+="&lastModified=on";
		}
		if(lastRead != null && (lastRead.equals("true") || lastRead.equals("on"))) {
			url+="&lastRead=on";
		}
		if(lastReadDays != null && (lastReadDays.equals("true") || lastReadDays.equals("on"))) {
			url+="&lastReadDays=on";
		}
		return url;
	}
	
}
