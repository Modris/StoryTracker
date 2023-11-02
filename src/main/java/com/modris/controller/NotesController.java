package com.modris.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.modris.model.Notes;
import com.modris.model.Tracker;
import com.modris.services.NotesService;
import com.modris.services.TrackerService;

@Controller
public class NotesController {
	
	private final NotesService notesService;
	private final TrackerService trackerService;
	
	@Autowired
	public NotesController(NotesService notesService, TrackerService trackerService) {
		this.notesService = notesService;
		this.trackerService = trackerService;
	}
	
	@GetMapping("/notes")
	public String goToNotesPage( @RequestParam("storyId") Long trackerId,@RequestParam("trackerName") String trackerName,Model model) {
		List<Notes> notesList = notesService.findAllById(trackerId);
		model.addAttribute("notesList", notesList); //listAll 
		model.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		model.addAttribute("trackerName", trackerName); //display trackerName as hard embedded value.

		return "notes.html"; //notes.html
	}
	@GetMapping("/page/notes")
	public String goToNotesPage2( @RequestParam("storyId") Long trackerId,
			@RequestParam("trackerName") String trackerName,
			Model model,
			RedirectAttributes redirectAttributes) {
		List<Notes> notesList = notesService.findAllById(trackerId);
		redirectAttributes.addAttribute("notesList", notesList); //listAll 
		redirectAttributes.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		redirectAttributes.addAttribute("trackerName", trackerName); //display trackerName as hard embedded value.

		return "redirect:/notes"; //notes.html
	}

	@PostMapping("/addNotes")
	public String addNotes( @RequestParam("name") String name,
							@RequestParam("comments") String comments,
							@RequestParam("storyId") Long trackerId,
							@RequestParam("trackerName") String trackerName,
							Model model,
							RedirectAttributes redirectAttributes) {
		Notes notes = new Notes(name,comments);
		Tracker tracker = trackerService.findById(trackerId);
		notes.setTracker(tracker);
		notesService.addNotes(notes);
		
		redirectAttributes.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		redirectAttributes.addAttribute("trackerName",trackerName);

		return "redirect:/notes";
	}
	
	@PostMapping("/deleteNote")
	public String deleteNoteById(@RequestParam("notesId") Long notesId,
								@RequestParam("storyId") Long trackerId,
								@RequestParam("trackerName") String trackerName,
								RedirectAttributes redirectAttributes) {
		
		notesService.deleteById(notesId);
		redirectAttributes.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		redirectAttributes.addAttribute("trackerName",trackerName);
		
		return "redirect:/notes";
	}

	@PostMapping("editNote")
	public String editNote(@RequestParam("notesId") Long id, Model model) {
		Notes note = notesService.findById(id);
		
		model.addAttribute("notesName", note.getName());
		model.addAttribute("notesId", note.getId());
		model.addAttribute("storyId", note.getTracker().getId());
		model.addAttribute("trackerName", note.getTracker().getName());
		model.addAttribute("notesComment", note.getComments());
		return "editNote.html";
	}
	@PostMapping("/updateNote")
	public String updateNote(   @RequestParam("notesName") String notesName,
								@RequestParam("notesComment") String notesComment,
								@RequestParam("notesId") Long notesId,
								@RequestParam("storyId") Long trackerId,
								@RequestParam("trackerName") String trackerName,
								RedirectAttributes redirectAttributes) {
		
		notesService.updateNoteHibernate(notesId,notesName,notesComment);
		
		redirectAttributes.addAttribute("storyId", trackerId); //id for when i'm gonna save later
		redirectAttributes.addAttribute("trackerName",trackerName);
		
		return "redirect:/notes";
	}
	
	
	
}
