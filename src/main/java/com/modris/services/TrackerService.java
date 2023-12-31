package com.modris.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.modris.model.Tracker;
import com.modris.repositories.TrackerRepository;

import jakarta.transaction.Transactional;

@Service
public class TrackerService {
	
	private final TrackerRepository trackerRepository;
	private final NotesService notesService;
	private final UserService userService;
	@Autowired
	public TrackerService(TrackerRepository trackerRepository, NotesService notesService, UserService userService) {
		this.trackerRepository = trackerRepository;
		this.notesService = notesService;
		this.userService = userService;
	}
	
	@Transactional
	public void addTracker(Tracker tracker) {
		tracker.setLastModified(LocalDateTime.now());
		trackerRepository.save(tracker);
	}
	
	
	public Page<Tracker> findAllPagedWithUserId(Long userId, int pageNumber, int pageSize, String sortField, String sortDirection){
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,
				sortDirection.equals("asc") ? Sort.by(sortField).ascending()
						: Sort.by(sortField).descending());
	
		return trackerRepository.findAllPagedWithUserId(pageable,userId);
		
	}
	
	public List<Tracker> findAllByUsernameNative(String username){
		List<Tracker> tList = trackerRepository.findAllByUsername(username);
		return tList;
	}
	

	@Transactional
	public void deleteByIdAndUserId(Long id, Long userId) {
		notesService.deleteByTrackerId(id);
		trackerRepository.deleteByIdAndUserId(id,userId);
	}
	
	public Tracker findByIdAndUserId(Long id, Long userId) {
		return trackerRepository.findByIdAndUserIdReturnTracker(id,userId);
	}
	@Transactional
	public void editSavedHibernate(Tracker tEdited,Long id, Long userId) {
		Tracker original = trackerRepository.findByIdAndUserIdReturnTracker(id, userId);
		original.setName(tEdited.getName());
		original.setCategory(tEdited.getCategory());
		original.setStatus(tEdited.getStatus());
		original.setProgress(tEdited.getProgress());
		original.setCreatedOn(tEdited.getCreatedOn());
		original.setLastModified(tEdited.getLastModified());
		trackerRepository.save(original);
	}
	

	
}
