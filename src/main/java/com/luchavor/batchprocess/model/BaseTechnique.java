package com.luchavor.batchprocess.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseTechnique {
	private ImportedTechniqueType type;
	private String mitreId; // t code for offensive techniques and d3 id for defensive techniques
	private String model;
	private String subModel;
	private String name;
	private String description;
	private String parentMitreId;
}