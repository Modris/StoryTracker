package com.modris.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.modris.model.Categories;

public interface CategoriesRepository extends CrudRepository<Categories,Long>{

	List<Categories> findAll();
	
	@Query("SELECT c FROM Categories c WHERE id = :id")
	Categories findByIdReturnCategories(@Param("id") Long id);
}
