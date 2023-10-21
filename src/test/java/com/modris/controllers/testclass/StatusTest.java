package com.modris.controllers.testclass;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "status")
public class StatusTest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String name;

	public StatusTest(){}
	
	public StatusTest(String name, Long id) {
		this.name = name;
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "Status [name=" + name + "]";
	}
	
	
	
	
}


