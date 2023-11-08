package com.modris.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.modris.model.Status;
import com.modris.repositories.StatusRepository;

import jakarta.transaction.Transactional;

@Service
public class StatusService {

	private final StatusRepository statusRepository;
	
	@Autowired
	public StatusService(StatusRepository statusRepository) {
		this.statusRepository = statusRepository;
	}
	
	@Transactional
	public List<Status> findAll(){
		return statusRepository.findAll();
	}
	
	public Status findByIdReturnStatus(Long id) {
		return statusRepository.findByIdReturnStatus(id);
	}
	
}
