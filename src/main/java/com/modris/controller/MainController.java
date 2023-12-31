package com.modris.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	public String defaultPageAuthorized() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
        						   && !"anonymousUser".equals(authentication.getPrincipal())) {
			return "redirect:/home";
		} else {
		return "defaultPage.html";
		}
	}
	@GetMapping("/home")
	public String mainPage(Model model, Principal principal) { 
		
		return viewPage(1,null,null,null,null,"5", "id", "asc",principal,model);

	}
	
	@GetMapping("/home/page/{pageNum}")
	public String viewPage(@PathVariable(name = "pageNum") int pageNum,
			@RequestParam(value = "createdOn",required=false) String createdOnAnswer,
			@RequestParam(value = "lastModified",required=false) String lastModifiedAnswer,
			@RequestParam(value = "lastRead",required=false) String lastReadAnswer,
			@RequestParam(value = "lastReadDays",required=false) String lastReadDaysAnswer,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			Principal principal,
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
	
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		
		Page<Tracker> paged = trackerService.findAllPagedWithUserId(userExtracted.getId(),pageNum, Integer.valueOf(pageSize), sortField,sortDir);
		
		List<Integer> indexListOfOriginal = indexList(username,paged); // for sorting purposes
		// i get original Tracker list sorted by id in ascending order.
		// and then this index list by which i can display the id's.
		model.addAttribute("indexList",indexListOfOriginal );
		
		List<Tracker> trackerListPaged = paged.getContent();
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", paged.getTotalPages());
		model.addAttribute("totalItems", paged.getTotalElements());
		model.addAttribute("trackerListPaged",trackerListPaged);
		
		
		return "Welcome.html";
	}
	
	public List<Integer> indexList(String username, Page<Tracker> paged){
		
		List<Integer> indexList = new ArrayList<>();
		List<Tracker> original = trackerService.findAllByUsernameNative(username);
	
		for(Tracker start:paged) {
			int index = original.lastIndexOf(start);
			indexList.add(index);
		}
		return indexList;
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
		t.setUser(userExtracted);
		
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
				Model model,
				Principal principal) {
			
			String username = principal.getName();
			Optional<Users> userInRepo = userService.findByUsername(username);
			Users userExtracted = userInRepo.get();
			
			Tracker t = trackerService.findByIdAndUserId(id, userExtracted.getId());
			if(t == null) {
				model.addAttribute("errorMsg", "Error. Can't edit other people data.");
				return "errorPage.html";
			} else {
			List<Status> statusList = statusService.findAll();
			List<Categories> categoriesList = categoriesService.findAll();
			

			model.addAttribute("createdOnDate", t.getCreatedOn());
			model.addAttribute("lastModifiedDate", t.getLastModified());
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
		}
	
	@PostMapping("editSaved")
	public String editSaved(Tracker tracker, @RequestParam("id") Long id,
			@RequestParam(value = "createdOnNoTrackerBind",required=false) String createdOn,
			@RequestParam(value = "lastModifiedNoTrackerBind",required=false) String lastModified,
			@RequestParam(value = "lastReadNoTrackerBind",required=false) String lastRead,
			@RequestParam(value = "lastReadDaysNoTrackerBind",required=false) String lastReadDays,
			@RequestParam(value="currentPage") String currentPage,
			@RequestParam(value = "pageSize",required=false) String pageSize,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			Principal principal,
			Model model)
			
			 {
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		Tracker trackerInRepo = trackerService.findByIdAndUserId(id, userExtracted.getId());
		if(trackerInRepo == null) {
			model.addAttribute("errorMsg", "Error. Can't edit other people data.");
			return "errorPage.html";
		} else {
		
		trackerService.editSavedHibernate(tracker,id,userExtracted.getId());
		
		return pagedUrl(currentPage,sortField,sortDir,pageSize, createdOn,lastModified,lastRead,lastReadDays);
			}
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
			@Param("sortDir") String sortDir,
			Principal principal,
			Model model) {
		if(id == null) {
			model.addAttribute("errorMsg", "Error. Id can't be null.");
			return "errorPage.html";
		}
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		Tracker trackerInRepo = trackerService.findByIdAndUserId(id, userExtracted.getId());
		if(trackerInRepo == null) {
			model.addAttribute("errorMsg", "Error. Can't delete other people data.");
			return "errorPage.html";
		} else {
		trackerService.deleteByIdAndUserId(id, userExtracted.getId());
		
		return pagedUrl(currentPage,sortField,sortDir, pageSize, createdOn,lastModified,lastRead,lastReadDays);
		}
	}

	private String pagedUrl(String currentPage,String sortField, String sortDir, 
			String pageSize,String createdOn,  String lastModified,String lastRead, 
			String lastReadDays) {
	
		
		String url = "redirect:/home/page/"+currentPage+"?sortField="+sortField+"&sortDir="+sortDir;
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
