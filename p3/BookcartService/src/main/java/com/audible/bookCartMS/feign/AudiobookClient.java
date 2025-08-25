package com.audible.bookCartMS.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.audible.bookCartMS.dto.AudioBookDTO;

@FeignClient("AUDIOBOOK-SERVICE")
public interface AudiobookClient {

    
	@PostMapping(value = "/audiobooks/list", produces = "application/json")
	public ResponseEntity<List<AudioBookDTO>> getAudiobooksByIds(@RequestBody  List<Integer> bookIds);
	
	
}
