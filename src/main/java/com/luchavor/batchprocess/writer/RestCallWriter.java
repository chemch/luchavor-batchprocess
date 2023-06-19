package com.luchavor.batchprocess.writer;

import org.slf4j.Logger;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestCallWriter<SingleTechnique> implements ItemWriter<Object> {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(RestCallWriter.class);
	
	@Override
	public void write(Chunk<?> chunk) throws Exception {
		log.info("RestCallWriter Posting Chunk of Items: " + chunk.toString());
		
		// post to create 
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:7000/single-technique", chunk.getItems(), String.class);
		
		// check status code (should be 201)
		if(!response.getStatusCode().is2xxSuccessful())
			log.error(response.getBody());
	}
}