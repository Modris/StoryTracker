package com.modris.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.modris.model.Tracker;

@Repository
public interface TrackerRepository extends JpaRepository<Tracker,Long>{

	@Query("SELECT t FROM Tracker t")
	List<Tracker> findAll();
	
	
	@Query("SELECT t FROM Tracker t WHERE t.id = :id")
	Tracker findByIdReturnTracker(@Param("id") Long id);
	
	@Modifying
	@Query("Update Tracker t SET t.name = :name,t.category.id = :categoryId, t.status.id = :statusId, t.createdOn = :createdOn, t.progress = :progress WHERE t.id = :id")
	void updateWithId(
			@Param("name") String name, 
			@Param("categoryId") Long categoryId, 
			@Param("statusId") Long statusId,
			@Param("createdOn") LocalDateTime createdOn,
			@Param("progress") String progress,
			@Param("id") Long id);

	

}
