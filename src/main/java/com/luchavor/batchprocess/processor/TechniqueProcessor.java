package com.luchavor.batchprocess.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.luchavor.batchprocess.model.SingleTechnique;

public class TechniqueProcessor implements ItemProcessor<SingleTechnique, SingleTechnique> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueProcessor.class);
	
	@Autowired
	RestTemplate restTemplate;

	@Override
	public SingleTechnique process(final SingleTechnique technique) throws Exception {
		// post to create 
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:7000/single-technique", technique, String.class);
		
		// check status code (should be 201)
		if(!response.getStatusCode().is2xxSuccessful())
			log.error(response.getBody());
		
		// return same object
		return technique;
	}
}