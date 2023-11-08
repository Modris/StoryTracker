package com.modris.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.modris.model.Notes;
import com.modris.repositories.NotesRepository;

import jakarta.transaction.Transactional;

@Service
public class NotesService {
	private final NotesRepository notesRepository;
	
	@Autowired
	public NotesService(NotesRepository notesRepository) {
		this.notesRepository = notesRepository;
	}
	@Transactional
	public void addNotes(Notes notes) {
		notesRepository.save(notes);
	}
	
	public List<Notes> findAllById(Long id){
		return notesRepository.findAllById(id);
	}
	
	public Notes findById(Long id) {
		return notesRepository.findById2(id);
	}
	public Notes findByIdAndTrackerId(Long id, Long trackerId) {
		return notesRepository.findByIdAndTrackerId(id,trackerId);
	}


	@Transactional
	public void deleteByTrackerId(Long trackerId) {
		notesRepository.deleteByTrackerId(trackerId);
	}
	
	@Transactional
	public void deleteByIdAndTrackerId(Long id, Long trackerId) {
		notesRepository.deleteByIdAndTrackerId(id,trackerId);
	}
	@Transactional
	public void updateNoteHibernate(Long notesId,String notesNameEdited, String notesCommentEdited) {
		Notes original = notesRepository.findById2(notesId);
		original.setName(notesNameEdited);
		original.setComments(notesCommentEdited);
		
		notesRepository.save(original);
	}
	@Transactional
	public void updateNote(Long notesId,String notesName, String notesComment) {
		notesRepository.updateNote(notesId,notesName,notesComment);
	}
}
