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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import com.luchavor.batchprocess.listener.TechniqueImportExecutionListener;
import com.luchavor.batchprocess.processor.TechniqueProcessor;
import com.luchavor.batchprocess.writer.RestApiWriter;
import com.luchavor.datamodel.event.Event;
import com.luchavor.datamodel.technique.ImportTechniqueItem;
import com.luchavor.datamodel.technique.TechniqueType;

@Configuration
public class TechniqueImportConfig {

	// tag::importMitreTechniqueDataJob[]
	@Bean
	FlatFileItemReader<ImportTechniqueItem> reader() {
		return new FlatFileItemReaderBuilder<ImportTechniqueItem>()
			.name("techniqueReader")
			.resource(new ClassPathResource("input/technique-data.csv"))
			.delimited()
			.names(new String[]{"model", "subModel", "mitreId", "tactic", "name", "description", "parentMitreId", "treeLevel", "type"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<ImportTechniqueItem>() {{ setTargetType(ImportTechniqueItem.class); }})
			.linesToSkip(1) // skip top line which has headers
			.build();
	}
	
	@Bean
	RestApiWriter<ImportTechniqueItem> techniqueItemRestApiWriter() {
		return new RestApiWriter<ImportTechniqueItem>(TechniqueType.SINGLE);
	}
	
	@Bean
	RestApiWriter<ImportTechniqueItem> techniqueGroupRestApiWriter() {
		return new RestApiWriter<ImportTechniqueItem>(TechniqueType.COMPOSITE);
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
			.<ImportTechniqueItem, ImportTechniqueItem> chunk(100, transactionManager)
			.reader(reader())
			.processor(new TechniqueProcessor(TechniqueType.SINGLE))
			.writer(techniqueItemRestApiWriter())
			.build();
	}
	
	// import composite techniques (technique groups)
	@Bean
	Step importTechniqueGroupsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importTechniqueGroupsStep", jobRepository)
			.<ImportTechniqueItem, ImportTechniqueItem> chunk(200, transactionManager)
			.reader(reader())
			.processor(new TechniqueProcessor(TechniqueType.COMPOSITE))
			.writer(techniqueGroupRestApiWriter())
			.build();
	}
	// end::importMitreTechniqueDataJob[]
}