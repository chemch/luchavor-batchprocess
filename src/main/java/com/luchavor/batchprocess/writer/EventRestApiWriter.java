package com.luchavor.batchprocess.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.luchavor.datamodel.event.EventType;
import lombok.Data;

@Data
public class EventRestApiWriter<Connection> implements ItemWriter<Object> {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(EventRestApiWriter.class);
	
	private final EventType eventType;
	
	@Override
	public void write(Chunk<?> chunk) throws Exception {
		log.debug("Bulk creating " + chunk.size() + " connection events");
		// build url
		String url =  "http://localhost:7000/batch/connection";
		// post to create 
		ResponseEntity<String> response = restTemplate.postForEntity(url, chunk.getItems(), String.class);
		// check status code (should be 2xx)
		if(!response.getStatusCode().is2xxSuccessful())
			log.error(response.getBody());
	}
}