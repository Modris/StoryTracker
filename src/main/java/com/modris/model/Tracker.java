package com.modris.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tracker")
public class Tracker {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String name;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category")
	private Categories category;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status")
	private Status status;
	
	//page number, chapter number, series number or whatever
	private String progress;
	
	@CreationTimestamp
	private LocalDateTime createdOn;

	@UpdateTimestamp
	private LocalDateTime lastModified;
	
	@PreUpdate
	public void onUpdate() {
		this.lastModified = LocalDateTime.now();
	}
	@Formula("(SELECT timestampdiff(HOUR, t.last_modified, current_timestamp()) FROM Tracker t WHERE t.id = id)")
	private Long lastReadHours;

	@Formula("(SELECT timestampdiff(DAY, t.last_modified, current_timestamp()) FROM Tracker t WHERE t.id = id)")
	private Long lastReadDays;

	
	//private Audit audit;
	
	
	public Tracker() {}
	
	public Tracker(Categories category, Status status) {
		this.category = category;
		this.status = status;
	}
	public Tracker(String name, Categories category, Status status, String progress) {
		this.name = name;
		this.category = category;
		this.status = status;
		this.progress = progress;
	}
	
	

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
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

	public LocalDate getCreatedOnConvertedToDate() {
		return this.createdOn.toLocalDate();
	}
	public LocalDate getLastModifiedConvertedToDate() {
		return this.lastModified.toLocalDate();
	}
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	

	public Long getLastReadHours() {
		return lastReadHours;
	}

	public void setLastReadHours(Long lastReadHours) {
		this.lastReadHours = lastReadHours;
	}

	public Long getLastReadDays() {
		return lastReadDays;
	}

	public void setLastReadDays(Long lastReadDays) {
		this.lastReadDays = lastReadDays;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tracker other = (Tracker) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Tracker [id=" + id + ", name=" + name + ", category=" + category + ", status=" + status + ", progress="
				+ progress + ", createdOn=" + createdOn + ", lastModified=" + lastModified + ", lastReadHours="
				+ lastReadHours + ", lastReadDays=" + lastReadDays + "]";
	}
	
	
	
}
