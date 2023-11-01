package com.modris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.modris.model.Authority;
import com.modris.model.UserRoles;

public interface UserRolesRepository extends JpaRepository<UserRoles,Long> {

	 
	@Query(value = 
			"""
			SELECT a.authority FROM user_authorities u
			JOIN  authority a ON u.authority = a.id
			JOIN users uu ON uu.id = u.user_id WHERE uu.username = :username
			"""
			,nativeQuery = true)
	List<Authority> getAuthoritiesWithUsername(@Param("username") String username);
/*
    SELECT a.authority FROM user_authorities u
JOIN  authority a ON u.authority = a.id
JOIN users uu ON uu.id = u.user_id WHERE uu.username = "abc";
	*/
}
