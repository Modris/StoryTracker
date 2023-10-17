package com.modris.repositories;

import org.springframework.data.repository.CrudRepository;

import com.modris.model.Users;

public interface UserRepository extends CrudRepository<Users,Long> {

	
}
