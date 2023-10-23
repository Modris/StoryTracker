package com.modris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.modris.model.Notes;

public interface NotesRepository extends CrudRepository<Notes,Long> {

	List<Notes> findAll();

	
	@Query("SELECT n FROM Notes n WHERE n.tracker.id = :id")
	List<Notes> findAllById(@Param("id") Long tracker_id);
	
	@Modifying
	@Query("UPDATE Notes n SET n.name = :notesName,n.comments = :notesComment WHERE n.id = :notesId")
	void updateNote(@Param("notesId") Long notesId,
				@Param("notesName") String notesName,
				@Param("notesComment") String notesComment);


	@Query("SELECT n FROM Notes n WHERE id = :id")
	Notes findById2(@Param("id") Long id);
}
