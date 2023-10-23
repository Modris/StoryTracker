package com.modris.model.projections;



import java.time.LocalDateTime;

import com.modris.model.Categories;
import com.modris.model.Status;
public interface TrackerProjection {


	Long getId();
	String getName();
	Categories getCategory();
	Status getStatus();
	LocalDateTime getCreatedOn();
	LocalDateTime getLastRead();
	
}
