package com.luchavor.batchprocess.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.luchavor.batchprocess.converter.TechniqueConverter;
import com.luchavor.batchprocess.model.ConvertedTechnique;
import com.luchavor.batchprocess.model.ImportedTechnique;
import com.luchavor.batchprocess.model.ImportedTechniqueType;

import lombok.Data;

@Data
public class TechniqueProcessor implements ItemProcessor<ImportedTechnique, ConvertedTechnique> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueProcessor.class);
	
	private final ImportedTechniqueType importedTechniqueType;
	
	@Override
	public ConvertedTechnique process(final ImportedTechnique techniqueItemImport) throws Exception {		
		// return only techniques that match the given type (simple or composite)
		if(techniqueItemImport.getType().equals(importedTechniqueType)) {
			return TechniqueConverter.convert(techniqueItemImport);
		} else {
			log.debug("Skipping technique due mismatched Type: " + importedTechniqueType + ": " + techniqueItemImport.getMitreId());
			return null;
		}
	}
}