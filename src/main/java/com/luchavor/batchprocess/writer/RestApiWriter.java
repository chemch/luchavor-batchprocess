package com.luchavor.batchprocess.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.luchavor.batchprocess.model.TechniqueType;
import lombok.Data;

@Data
public class RestApiWriter<SingleTechnique> implements ItemWriter<Object> {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(RestApiWriter.class);
	
	private final TechniqueType techniqueType;
	
	@Override
	public void write(Chunk<?> chunk) throws Exception {
		log.info("RestApiWriter Posting Chunk of Items: " + chunk.toString());
		// build url
		String url =  String.format("http://localhost:7000/batch/%s-technique", techniqueType.toString().toLowerCase());
		// post to create 
		ResponseEntity<String> response = restTemplate.postForEntity(url, chunk.getItems(), String.class);
		// check status code (should be 201)
		if(!response.getStatusCode().is2xxSuccessful())
			log.error(response.getBody());
	}
}