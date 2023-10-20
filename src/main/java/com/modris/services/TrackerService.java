package com.modris.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Transactional
	public void deleteById(Long id) {
		trackerRepository.deleteById(id);
	}
	
	public Tracker findById(Long id) {
		return trackerRepository.findByIdReturnTracker(id);
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
