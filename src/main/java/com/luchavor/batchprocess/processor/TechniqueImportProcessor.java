package com.luchavor.batchprocess.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import com.luchavor.batchprocess.model.TechniqueImport;
import com.luchavor.batchprocess.model.TechniqueImportType;

import lombok.Data;

@Data
public class TechniqueImportProcessor implements ItemProcessor<TechniqueImport, TechniqueImport> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueImportProcessor.class);
	
	private final TechniqueImportType techniqueImportType;
	
	@Override
	public TechniqueImport process(final TechniqueImport techniqueItemImport) throws Exception {		
		// return only techniques that match the given type (simple or composite)
		if(techniqueItemImport.getType().equals(techniqueImportType)) {
			return techniqueItemImport;
		} else {
			log.debug("Skipping technique due mismatched Type: " + techniqueImportType + ": " + techniqueItemImport.getMitreId());
			return null;
		}
	}
}