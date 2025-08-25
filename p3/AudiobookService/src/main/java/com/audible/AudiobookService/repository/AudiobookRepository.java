package com.audible.AudiobookService.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.audible.AudiobookService.model.AudioBookDTO;
import com.audible.AudiobookService.model.Audiobook;

@Repository
public interface AudiobookRepository extends JpaRepository<Audiobook, Integer> {

	List<Audiobook> findAllByLanguage(String language);

	Optional<Audiobook> findByTitle(String title);

	@Query("SELECT NEW com.audible.AudiobookService.model.AudioBookDTO(" +
	          "a.bookId, a.title, a.author, a.narrator, a.time, a.price," +
	          "a.release_date, a.language, a.stars, a.ratings, a.audioData) " +
	          "FROM Audiobook a")
	List<AudioBookDTO> findAllAudioBookDTO();

	

}
