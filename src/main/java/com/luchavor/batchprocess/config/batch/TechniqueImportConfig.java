package com.luchavor.batchprocess.config.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import com.luchavor.batchprocess.listener.TechniqueImportExecutionListener;
import com.luchavor.batchprocess.model.ConvertedTechnique;
import com.luchavor.batchprocess.model.ImportedTechnique;
import com.luchavor.batchprocess.model.ImportedTechniqueType;
import com.luchavor.batchprocess.processor.TechniqueProcessor;
import com.luchavor.batchprocess.writer.TechniqueRestApiWriter;

@Configuration
public class TechniqueImportConfig {

	// tag::importMitreTechniqueDataJob[]
	@Bean
	FlatFileItemReader<ImportedTechnique> techniqueItemReader() {
		return new FlatFileItemReaderBuilder<ImportedTechnique>()
			.name("techniqueReader")
			.resource(new ClassPathResource("input/technique-data.csv"))
			.delimited()
			.names(new String[]{"model", "subModel", "mitreId", "tactic", "name", "description", "parentMitreId", "type"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<ImportedTechnique>() {{ setTargetType(ImportedTechnique.class); }})
			.linesToSkip(1) // skip top line which has headers
			.build();
	}
	
	@Bean
	TechniqueRestApiWriter<ConvertedTechnique> techniqueItemRestApiWriter() {
		return new TechniqueRestApiWriter<ConvertedTechnique>(ImportedTechniqueType.SINGLE);
	}
	
	@Bean
	TechniqueRestApiWriter<ConvertedTechnique> techniqueGroupRestApiWriter() {
		return new TechniqueRestApiWriter<ConvertedTechnique>(ImportedTechniqueType.COMPOSITE);
	}

	@Bean
	Job importMitreTechniqueData(JobRepository jobRepository, TechniqueImportExecutionListener techniqueImportExecutionListener, Step importTechniquesStep, 
			Step importTechniqueGroupsStep) {
		return new JobBuilder("importMitreTechniqueData", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(techniqueImportExecutionListener)
			.start(importTechniquesStep)
			.next(importTechniqueGroupsStep)
			.build();
	}
	
	// import single techniques
	@Bean
	Step importTechniquesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importTechniquesStep", jobRepository)
			.<ImportedTechnique, ConvertedTechnique> chunk(200, transactionManager)
			.reader(techniqueItemReader())
			.processor(new TechniqueProcessor(ImportedTechniqueType.SINGLE))
			.writer(techniqueItemRestApiWriter())
			.build();
	}
	
	// import composite techniques (technique groups)
	@Bean
	Step importTechniqueGroupsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importTechniqueGroupsStep", jobRepository)
			.<ImportedTechnique, ConvertedTechnique> chunk(200, transactionManager)
			.reader(techniqueItemReader())
			.processor(new TechniqueProcessor(ImportedTechniqueType.COMPOSITE))
			.writer(techniqueGroupRestApiWriter())
			.build();
	}
	// end::importMitreTechniqueDataJob[]
}