package com.luchavor.batchprocess.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.luchavor.batchprocess.model.ImportedTechniqueType;
import lombok.Data;

@Data
public class TechniqueRestApiWriter<ConvertedTechnique> implements ItemWriter<Object> {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(TechniqueRestApiWriter.class);
	
	private final ImportedTechniqueType importedTechniqueType;
	
	@Value("${neo4j-api-server.port}")
	private Integer apiServerPort;
	
	@Override
	public void write(Chunk<?> chunk) throws Exception {
		log.debug("Bulk creating " + chunk.size() + " techniques for type: " + importedTechniqueType.toString());
		// build url
		String url =  "http://localhost:" + apiServerPort.toString() + "/technique/%s".formatted(importedTechniqueType.toString().toLowerCase());
		// post to create 
		ResponseEntity<String> response = restTemplate.postForEntity(url, chunk.getItems(), String.class);
		// check status code (should be 2xx)
		if(!response.getStatusCode().is2xxSuccessful())
			log.error(response.getBody());
	}
}