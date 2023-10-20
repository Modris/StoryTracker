package com.modris.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

//First time ever Selenium Test. I need Docker Compose and isolated container
// to run Selenium properly. I'll do that and go more in depth when i have 
// REST API app to containerize and deploy to cloud. 
//No Selenium in this project unless i add REST API's in the future.
public class FirefoxFormTest {

	private WebDriver driver;
	
	@BeforeEach
	void setUp() {
		driver = new FirefoxDriver();
		
	}
	
	@AfterEach
	void tearDown() {
		driver.quit();
	}
	
	@Test
	@DisplayName("Button Add Story Works on Firefox.")
	void addStoryButtonTest() {
		driver.get("http://localhost:8080/");
		
		
	}
}
