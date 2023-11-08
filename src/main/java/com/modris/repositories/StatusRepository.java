package com.modris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.modris.model.Status;

public interface StatusRepository extends CrudRepository<Status,Long> {

	List<Status> findAll();
	
	@Query("SELECT s FROM Status s WHERE id = :id")
	Status findByIdReturnStatus(@Param("id") Long id);
}
