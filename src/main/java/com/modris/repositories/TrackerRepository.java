package com.modris.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.modris.model.Tracker;

@Repository
public interface TrackerRepository extends CrudRepository<Tracker, Long>{

	List<Tracker> findAll();
}
