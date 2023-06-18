package com.luchavor.batchprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@SuppressWarnings("hiding")
public class NoOpItemWriter<SingleTechnique> implements ItemWriter<Object> {
	
	private static final Logger log = LoggerFactory.getLogger(NoOpItemWriter.class);
	
	@Override
	public void write(Chunk<?> chunk) throws Exception {
		log.info("NoOpItemWriter Skipping: " + chunk.toString());
	}
}