package com.modris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.modris.model.Notes;

public interface NotesRepository extends JpaRepository<Notes,Long> {

	List<Notes> findAll();

	
	@Query("SELECT n FROM Notes n WHERE n.tracker.id = :id")
	List<Notes> findAllById(@Param("id") Long tracker_id);
	
	@Modifying
	@Query("UPDATE Notes n SET n.name = :notesName,n.comments = :notesComment WHERE n.id = :notesId")
	void updateNote(@Param("notesId") Long notesId,
				@Param("notesName") String notesName,
				@Param("notesComment") String notesComment);

	@Modifying
	@Query("DELETE FROM Notes n WHERE n.tracker.id = :trackerId")
	void deleteByTrackerId(@Param("trackerId") Long trackerId);

	@Query("SELECT n FROM Notes n WHERE n.id = :id")
	Notes findById2(@Param("id") Long id);
	
	@Query("SELECT n FROM Notes n WHERE n.id = :id AND n.tracker.id = :trackerId")
	Notes findByIdAndTrackerId(@Param("id") Long id, @Param("trackerId") Long trackerId);
	
	
	//@Modifying
	//@Query("DELETE FROM Notes n WHERE n.id = :id AND n.tracker = :trackerId")
	//void deleteByIdAndTrackerId(@Param("id") Long id, @Param("trackerId") Long trackerId);
}
