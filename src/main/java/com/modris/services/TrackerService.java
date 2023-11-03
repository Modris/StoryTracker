package com.modris.services;

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
	@Autowired
	public TrackerService(TrackerRepository trackerRepository, NotesService notesService) {
		this.trackerRepository = trackerRepository;
		this.notesService = notesService;
	}
	
	@Transactional
	public void addTracker(Tracker tracker) {
		trackerRepository.save(tracker);
	}
	
	
	public Page<Tracker> findAllPaged(Long userId, int pageNumber, int pageSize, String sortField, String sortDirection){
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,
				sortDirection.equals("asc") ? Sort.by(sortField).ascending()
						: Sort.by(sortField).descending());
		
		return trackerRepository.findAllPagedWithUserId(pageable,userId);
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
		trackerRepository.save(original);
	}
	

	
}
