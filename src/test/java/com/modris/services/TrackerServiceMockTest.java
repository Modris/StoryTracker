package com.modris.services;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.modris.model.Categories;
import com.modris.model.Status;
import com.modris.model.Tracker;
import com.modris.repositories.TrackerRepository;

import jakarta.transaction.Transactional;
@ExtendWith(MockitoExtension.class)
public class TrackerServiceMockTest {

	@Mock
	private TrackerRepository trackerRepository;
	
	@InjectMocks
	private TrackerService trackerService;
	
	@Test
	@Transactional
	@DisplayName("TestService save method test with mocked trackerRepository")
	void trackerServiceSaveTestHappyFlow() {

		//given
		Tracker t = new Tracker("Pulp Fiction", new Categories("Movies"),
						new Status("Completed"), "Finished");
		
		List<Tracker> tList = new ArrayList<>();
		tList.add(t);
		
		when(trackerRepository.findAll()).thenReturn(tList);
		
		List<Tracker> tyrone = trackerRepository.findAll();

		assertAll(
			() -> assertEquals(1,tyrone.size()),
			() ->assertEquals("Pulp Fiction", tyrone.get(0).getName()),
			() ->assertEquals("Movies",tyrone.get(0).getCategory().getName()),
			() ->assertEquals("Finished",tyrone.get(0).getProgress())
		);
	}
	
}
