package com.modris.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name="notes")
public class Notes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="tracker_id")
	private Tracker tracker;
	
	private String comments;
	
	@CreationTimestamp
	private LocalDateTime createdOn;

	@UpdateTimestamp
	private LocalDateTime lastModified;

	@PreUpdate
	public void onUpdate() {
		this.lastModified = LocalDateTime.now();
	}
	
	@Formula("(SELECT timestampdiff(DAY, t.last_modified, current_timestamp()) FROM notes n WHERE n.id = id)")
	private Long lastRead;
	
	public Notes() {}
	
	public Notes(String name, String comments) {
		this.name = name;
		this.comments = comments;
	}

	
	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public LocalDate getCreatedOnConvertedToDate() {
		return this.createdOn.toLocalDate();
	}
	public LocalDate getLastModifiedConvertedToDate() {
		return this.lastModified.toLocalDate();
	}
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}


	public Long getLastRead() {
		return lastRead;
	}

	public void setLastRead(Long lastRead) {
		this.lastRead = lastRead;
	}

	public Tracker getTracker() {
		return tracker;
	}

	public void setTracker(Tracker tracker) {
		this.tracker = tracker;
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
	
}
