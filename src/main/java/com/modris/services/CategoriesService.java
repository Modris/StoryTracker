package com.modris.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.modris.model.Categories;
import com.modris.repositories.CategoriesRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoriesService {
	
	private final CategoriesRepository categoriesRepository;

	@Autowired
	public CategoriesService(CategoriesRepository categoriesRepository) {
		this.categoriesRepository = categoriesRepository;
	}
	
	@Transactional
	public void addCategory(Categories category) {
		categoriesRepository.save(category);
	}
	
	public void removeCategory(Categories category) {
		long id = category.getId();
		if(id>4) {
			categoriesRepository.deleteById(id);
		} else {
			System.out.println("Cannot delete First 4 Built in categories.");
		}
	}

	@Transactional
	public List<Categories> findAll(){
		return categoriesRepository.findAll();
	}
	
	public Categories findByIdReturnCategories(Long id) {
		return categoriesRepository.findByIdReturnCategories(id);
	}
}
