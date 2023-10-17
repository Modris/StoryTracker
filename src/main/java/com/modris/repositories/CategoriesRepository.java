package com.modris.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.modris.model.Categories;

public interface CategoriesRepository extends CrudRepository<Categories,Long>{

	List<Categories> findAll();
	
}
