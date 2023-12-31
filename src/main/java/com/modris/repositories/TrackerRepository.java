package com.modris.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	//save All id1 id 2.
	//future method?
	
	@Query("SELECT t FROM Tracker t WHERE t.id = :id AND t.user.id = :userId")
	Tracker findByIdAndUserIdReturnTracker(@Param("id") Long id,  @Param("userId") Long userId);
	
	@Modifying
	@Query("Update Tracker t SET t.name = :name,t.category.id = :categoryId, t.status.id = :statusId, t.createdOn = :createdOn, t.progress = :progress WHERE t.id = :id")
	void updateWithId(
			@Param("name") String name, 
			@Param("categoryId") Long categoryId, 
			@Param("statusId") Long statusId,
			@Param("createdOn") LocalDateTime createdOn,
			@Param("progress") String progress,
			@Param("id") Long id);

	@Query("SELECT t FROM Tracker t WHERE t.user.id = :userId")
	Page<Tracker> findAllPagedWithUserId(Pageable pageable, @Param("userId") Long id);
	
	@Modifying
	@Query("DELETE FROM Tracker t WHERE t.id = :id AND t.user.id = :userId")
	void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
/*
	@Query(value = """
			SELECT t.id,name,category,status,progress,created_on,last_modified,last_read_days,last_read_hours,t.user_id FROM tracker t
			JOIN users u ON t.user_id = u.id 
			WHERE u.username = :username
			""", nativeQuery = true)
	List<Tracker> findAllByUsernameNative(@Param("username") String username);
*/
	// Generates very long query. But if i use native Query then i have to do many extra steps.
	// Hard to say if fetching list + updating last_read values, then saving them into database.
	// is more performant than 1 long query.
	@Query("SELECT t FROM Tracker t WHERE t.user.username = :username")
	List<Tracker> findAllByUsername(@Param("username") String username);
	
}
	