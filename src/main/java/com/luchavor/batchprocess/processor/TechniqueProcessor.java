package com.luchavor.batchprocess.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.luchavor.datamodel.technique.InputTechnique;
import com.luchavor.datamodel.technique.TechniqueType;

import lombok.Data;

@Data
public class TechniqueProcessor implements ItemProcessor<InputTechnique, InputTechnique> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueProcessor.class);
	
	private final TechniqueType techniqueType;
	
	@Override
	public InputTechnique process(final InputTechnique inputTechnique) throws Exception {		
		// return only techniques that match the given type (simple or composite)
		if(inputTechnique.getType().toUpperCase().equals(techniqueType.toString())) {
			return inputTechnique;
		} else {
			log.debug("Skipping InputTechnique due to mismatched InputTechnique Type: " + techniqueType + ": " + inputTechnique.getMitreId());
			return null;
		}
	}
}