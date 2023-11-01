package com.modris.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.modris.model.Users;

public interface UserRepository extends CrudRepository<Users,Long> {

	@Query("SELECT u FROM Users u WHERE u.username = :username")
	Optional<Users> findByUsername(@Param("username") String username);
	
}
