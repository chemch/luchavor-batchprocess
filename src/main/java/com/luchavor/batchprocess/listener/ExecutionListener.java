package com.luchavor.batchprocess.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExecutionListener implements JobExecutionListener {
	
	@Autowired
	RestTemplate restTemplate;

	// call api to delete all technique objects in the neo4j db before starting
	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("ExecutionListener Removing Technique Data Before Job");
		// build url
		String url = "http://localhost:7000/batch/technique".formatted();
		// delete data using url
		restTemplate.delete(url);		
	}
	
	// call api to build relationships between techniques
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("ExecutionListener Calling API to Build Technique Relations");
			// build url
			String url =  "http://localhost:7000/relation/technique".formatted();
			// post to create 
			ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
			// check status code (should be 201)
			if(!response.getStatusCode().is2xxSuccessful())
				log.error(response.getBody());
		}
	}
}