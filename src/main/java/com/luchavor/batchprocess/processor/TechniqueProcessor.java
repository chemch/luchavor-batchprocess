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
	
	@Override
	public SingleTechnique process(final SingleTechnique technique) throws Exception {		
		// return same object
		return technique;
	}
}