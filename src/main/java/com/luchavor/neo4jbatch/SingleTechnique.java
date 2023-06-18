package com.luchavor.neo4jbatch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleTechnique {
	private String mitreId;
	private String tactic;
	private String name;
	private String description;
}