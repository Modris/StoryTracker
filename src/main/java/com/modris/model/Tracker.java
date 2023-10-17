package com.modris.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tracker")
public class Tracker {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String name;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category")
	private Categories category;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status")
	private Status status;
	
	//page number, chapter number, series number or whatever
	private String progress;
	
	@CreationTimestamp
	private LocalDateTime createdOn;

	@UpdateTimestamp
	private LocalDateTime lastRead;
	
	public Tracker() {}
	
	public Tracker(Categories category, Status status) {
		this.category = category;
		this.status = status;
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

	public Categories getCategory() {
		return category;
	}

	public void setCategory(Categories category) {
		this.category = category;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}


	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getLastRead() {
		return lastRead;
	}

	public void setLastRead(LocalDateTime lastRead) {
		this.lastRead = lastRead;
	}

	@Override
	public String toString() {
		return "Tracker [id=" + id + ", name=" + name + ", category=" + category + ", status=" + status + ", progress="
				+ progress + ", createdOn=" + createdOn + ", lastRead=" + lastRead + ", getId()=" + getId()
				+ ", getName()=" + getName() + ", getCategory()=" + getCategory() + ", getStatus()=" + getStatus()
				+ ", getProgress()=" + getProgress() + ", getCreatedOn()=" + getCreatedOn() + ", getLastRead()="
				+ getLastRead() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
	
}
