package com.luchavor.batchprocess.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.luchavor.datamodel.technique.ImportTechniqueItem;
import com.luchavor.datamodel.technique.TechniqueType;

import lombok.Data;

@Data
public class TechniqueProcessor implements ItemProcessor<ImportTechniqueItem, ImportTechniqueItem> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueProcessor.class);
	
	private final TechniqueType techniqueType;
	
	@Override
	public ImportTechniqueItem process(final ImportTechniqueItem importTechniqueItem) throws Exception {		
		// return only techniques that match the given type (simple or composite)
		if(importTechniqueItem.getType().equals(techniqueType)) {
			return importTechniqueItem;
		} else {
			log.debug("Skipping technique due mismatched Type: " + techniqueType + ": " + importTechniqueItem.getMitreId());
			return null;
		}
	}
}