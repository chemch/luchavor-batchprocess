package com.luchavor.batchprocess.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import com.luchavor.batchprocess.model.Technique;
import com.luchavor.batchprocess.model.TechniqueType;

import lombok.Data;

@Data
public class TechniqueProcessor implements ItemProcessor<Technique, Technique> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueProcessor.class);
	
	private final TechniqueType techniqueType;
	
	@Override
	public Technique process(final Technique technique) throws Exception {		
		// return only techniques that match the given type (simple or composite)
		if(technique.getType().toUpperCase().equals(techniqueType.toString())) {
			return technique;
		} else {
			log.debug("Skipping Technique due to mismatched Technique Type: " + techniqueType + ": " + technique.getMitreId());
			return null;
		}
	}
}