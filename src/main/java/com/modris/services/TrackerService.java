package com.modris.services;

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
	
	
	//create
	//read
	//update
	//delete
	// but first things first. We need some BASE status, categories.
	
	
}
