package com.luchavor.batchprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class TechniqueProcessor implements ItemProcessor<SingleTechnique, SingleTechnique> {

	private static final Logger log = LoggerFactory.getLogger(TechniqueProcessor.class);

	@Override
	public SingleTechnique process(final SingleTechnique technique) throws Exception {
		final String mitreId = technique.getMitreId().toUpperCase();
		final String tactic = technique.getTactic().toUpperCase();
		final String name = technique.getName().toUpperCase();
		final String description = technique.getDescription().toUpperCase();
		final SingleTechnique transformed = new SingleTechnique(mitreId, tactic, name, description);
		log.info("Converting (" + technique + ") into (" + transformed + ")");
		return transformed;
	}
}