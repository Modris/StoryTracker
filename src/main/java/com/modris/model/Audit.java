package com.modris.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Audit {

    @Column(name = "created_on")
    private LocalDateTime createdOn;
 

    @Column(name = "last_read")
    private LocalDateTime lastRead;


	public Audit() {}
	
	
	public Audit getAudit() {
		Audit audit = new Audit();
		audit.setCreatedOn(this.createdOn);
		audit.setLastRead(this.lastRead);
		return audit;
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
    
    
	
}
