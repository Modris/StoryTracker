package com.modris.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.modris.model.Notes;
import com.modris.model.Tracker;
import com.modris.model.Users;
import com.modris.services.NotesService;
import com.modris.services.TrackerService;
import com.modris.services.UserService;

@Controller
public class NotesController {
	
	private final NotesService notesService;
	private final TrackerService trackerService;
	private final UserService userService;
	@Autowired
	public NotesController(NotesService notesService, TrackerService trackerService,
			UserService userService) {
		this.notesService = notesService;
		this.trackerService = trackerService;
		this.userService = userService;
	}
	
	
	@GetMapping("/home/notes")
	public String goToNotesPage( @RequestParam("storyId") Long trackerId,
			@RequestParam("trackerName") String trackerName,
			Model model,
			Principal principal) {
		
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		Tracker trackerInRepo = trackerService.findByIdAndUserId(trackerId, userExtracted.getId());
		if(trackerInRepo == null) {
			model.addAttribute("errorMsg", "Error. Can't access other people data.");
			return "errorPage.html";
		} else {
		List<Notes> notesList = notesService.findAllByTrackerId(trackerId);
		model.addAttribute("notesList", notesList); //listAll 
		model.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		model.addAttribute("trackerName", trackerName); //display trackerName as hard embedded value.

		return "notes.html"; //notes.html
		}
	}

	@PostMapping("/home/addNotes")
	public String addNotes( @RequestParam("name") String name,
							@RequestParam("comments") String comments,
							@RequestParam("storyId") Long trackerId,
							@RequestParam("trackerName") String trackerName,
							Model model,
							RedirectAttributes redirectAttributes,
							Principal principal) {
		Notes notes = new Notes(name,comments);
		
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		
		Tracker tracker = trackerService.findByIdAndUserId(trackerId, userExtracted.getId());
		if(tracker == null) {
			model.addAttribute("errorMsg", "Error. Can't add notes for other people.");
			 return "errorPage.html";
		} else {
		notes.setTracker(tracker);
		notesService.addNotes(notes);
		
		redirectAttributes.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		redirectAttributes.addAttribute("trackerName",trackerName);

		return "redirect:/home/notes";
		}
	}
	
	@PostMapping("/deleteNote")
	public String deleteNoteById(@RequestParam("notesId") Long notesId,
								@RequestParam("storyId") Long trackerId,
								@RequestParam("trackerName") String trackerName,
								RedirectAttributes redirectAttributes,
								Principal principal,
								Model model) {
		
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		Tracker tracker = trackerService.findByIdAndUserId(trackerId, userExtracted.getId());
		if(tracker == null) {
			model.addAttribute("errorMsg", "Error. Can't delete notes for other people.");
			 return "errorPage.html";
		} else {
		notesService.deleteByIdAndTrackerId(notesId, trackerId);
		redirectAttributes.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		redirectAttributes.addAttribute("trackerName",trackerName);
		
		return "redirect:/home/notes";
		}
	}

	@PostMapping("/home/editNote")
	public String editNote(@RequestParam("notesId") Long id, Model model, Principal principal) {
		

		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		
		Notes note = notesService.findById(id);
		Long trackerId = note.getTracker().getId();
		Tracker tracker = trackerService.findByIdAndUserId(trackerId, userExtracted.getId());
		Notes n = notesService.findByIdAndTrackerId(id,trackerId);
		
		if(tracker == null || n == null) {
			model.addAttribute("errorMsg", "Error. Can't edit notes from other people.");
			 return "errorPage.html";
		} else {
		
		model.addAttribute("notesName", note.getName());
		model.addAttribute("notesId", note.getId());
		model.addAttribute("storyId", note.getTracker().getId());
		model.addAttribute("trackerName", note.getTracker().getName());
		model.addAttribute("notesComment", note.getComments());
		return "editNote.html";
		}
	}
	@PostMapping("/home/updateNote")
	public String updateNote(   @RequestParam("notesName") String notesName,
								@RequestParam("notesComment") String notesComment,
								@RequestParam("notesId") Long notesId,
								@RequestParam("storyId") Long trackerId,
								@RequestParam("trackerName") String trackerName,
								RedirectAttributes redirectAttributes,
								Model model,
								Principal principal) {
		
		String username = principal.getName();
		Optional<Users> userInRepo = userService.findByUsername(username);
		Users userExtracted = userInRepo.get();
		
		Tracker tracker = trackerService.findByIdAndUserId(trackerId, userExtracted.getId());
		Notes n = notesService.findByIdAndTrackerId(notesId,trackerId);
		if(tracker == null || n == null) {
			model.addAttribute("errorMsg", "Error. Can't update notes from other people.");
			 return "errorPage.html";
		} else {
			
		notesService.updateNoteHibernate(notesId,notesName,notesComment);
		
		redirectAttributes.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		redirectAttributes.addAttribute("trackerName",trackerName);
		
		return "redirect:/home/notes";
		}
	}
	
	
	
}
