package com.modris.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import com.modris.model.Categories;
import com.modris.repositories.CategoriesRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoriesServiceTests {

	
	@Autowired
	private CategoriesService categoriesService;
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	
	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0").withDatabaseName("testcontainers2")
			.withUsername("sa").withPassword("123");

	static {
		mySQLContainer.start();
	}
	
	@Test
	@Transactional
	@DisplayName("findByIdReturnCategories method test. Happy flow.")
	public void findByIdReturnCategoriesTest() {
		
		//Movies Books TV-Shows Comic books
		Categories movies = categoriesService.findByIdReturnCategories(1L);
		Categories books = categoriesService.findByIdReturnCategories(2L);
		Categories tv_shows = categoriesService.findByIdReturnCategories(3L);
		Categories comic_books = categoriesService.findByIdReturnCategories(4L);
		
		assertAll(
				()-> assertEquals("Movies", movies.getName()),
				()-> assertEquals("Books", books.getName()),
				()-> assertEquals("TV-Shows", tv_shows.getName()),
				()->assertEquals("Comic books", comic_books.getName())
				);
	}
	
}
