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
	
	@Autowired
	public TrackerService(TrackerRepository trackerRepository) {
		this.trackerRepository = trackerRepository;
	}
	
	@Transactional
	public void addTracker(Tracker tracker) {
		trackerRepository.save(tracker);
	}
	
	public List<Tracker> findAll() {
		return trackerRepository.findAll();
	}
	
	public Page<Tracker> findAllPaged(int pageNumber, int pageSize, String sortField, String sortDirection){
		Pageable pageable = PageRequest.of(pageNumber-1, pageSize,
				sortDirection.equals("asc") ? Sort.by(sortField).ascending()
						: Sort.by(sortField).descending());
		
		return trackerRepository.findAll(pageable);
	}
	
	
	@Transactional
	public void deleteById(Long id) {
		trackerRepository.deleteById(id);
	}
	
	public Tracker findById(Long id) {
		return trackerRepository.findByIdReturnTracker(id);
	}
	@Transactional
	public void editSavedHibernate(Tracker tEdited,Long id) {
		Tracker original = trackerRepository.findByIdReturnTracker(id);
		original.setName(tEdited.getName());
		original.setCategory(tEdited.getCategory());
		original.setStatus(tEdited.getStatus());
		original.setProgress(tEdited.getProgress());
		trackerRepository.save(original);
	}
	
	@Transactional
	public void editSaved(String name,
			Long categoryId, 
			Long statusId,
			LocalDateTime createdOn,
			String progress,
			Long id) {
		trackerRepository.updateWithId(name,
				categoryId,
				statusId,
				createdOn,
				progress,
				id);
	}
	//create
	//read
	//update
	//delete
	// but first things first. We need some BASE status, categories.
	
	
}
