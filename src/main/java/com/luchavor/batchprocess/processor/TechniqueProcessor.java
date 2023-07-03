package com.luchavor.batchprocess.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.luchavor.datamodel.technique.TechniqueItemImport;
import com.luchavor.datamodel.technique.TechniqueType;

import lombok.Data;

@Data
public class TechniqueProcessor implements ItemProcessor<TechniqueItemImport, TechniqueItemImport> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueProcessor.class);
	
	private final TechniqueType techniqueType;
	
	@Override
	public TechniqueItemImport process(final TechniqueItemImport techniqueItemImport) throws Exception {		
		// return only techniques that match the given type (simple or composite)
		if(techniqueItemImport.getType().equals(techniqueType)) {
			return techniqueItemImport;
		} else {
			log.debug("Skipping technique due mismatched Type: " + techniqueType + ": " + techniqueItemImport.getMitreId());
			return null;
		}
	}
}