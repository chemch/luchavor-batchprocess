package com.luchavor.batchprocess.converter;

import com.luchavor.batchprocess.model.ConvertedTechnique;
import com.luchavor.batchprocess.model.ImportedTechnique;

public class TechniqueConverter {
	public static ConvertedTechnique convert(ImportedTechnique importedTechnique) {
		// create return value
		ConvertedTechnique rv = new ConvertedTechnique();
		rv.setType(importedTechnique.getType());
		rv.setMitreId(importedTechnique.getMitreId());
		rv.setModel(importedTechnique.getModel());
		rv.setSubModel(importedTechnique.getSubModel());
		// parse string to array of strings
		rv.setTactic(parseRawTactic(importedTechnique.getTactic()));
		rv.setName(importedTechnique.getName());
		rv.setDescription(importedTechnique.getDescription());
		rv.setParentMitreId(importedTechnique.getParentMitreId());
		// return value
		return rv;
	}
	
	private static String[] parseRawTactic(String parseValue) {
		return parseValue.split(", ");
	}
}