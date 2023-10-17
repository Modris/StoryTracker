package com.modris.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.modris.model.Status;

public interface StatusRepository extends CrudRepository<Status,Long> {

	List<Status> findAll();
}
