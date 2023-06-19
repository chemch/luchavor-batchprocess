package com.luchavor.batchprocess.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import com.luchavor.batchprocess.model.Technique;
import com.luchavor.batchprocess.model.TechniqueType;
import com.luchavor.batchprocess.processor.TechniqueProcessor;
import com.luchavor.batchprocess.writer.RestApiWriter;

@Configuration
public class BatchConfiguration {
	
	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	// tag::readerwriterprocessor[]
	@Bean
	FlatFileItemReader<Technique> reader() {
		return new FlatFileItemReaderBuilder<Technique>()
			.name("techniqueReader")
			.resource(new ClassPathResource("technique-data.csv"))
			.delimited()
			.names(new String[]{"model", "mitreId", "tactic", "name", "description", "parentMitreId", "treeLevel", "type"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Technique>() {{ setTargetType(Technique.class); }})
			.linesToSkip(1) // skip top line which has headers
			.build();
	}
	
	@Bean
	RestApiWriter<Technique> techniqueWriter() {
		return new RestApiWriter<Technique>(TechniqueType.SINGLE);
	}
	
	@Bean
	RestApiWriter<Technique> compositeWriter() {
		return new RestApiWriter<Technique>(TechniqueType.COMPOSITE);
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	Job importTechniqueJob(JobRepository jobRepository, Step importTechniquesStep, Step importCompositesStep) {
		return new JobBuilder("importTechniqueJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(importCompositesStep)
			.next(importTechniquesStep)
			.build();
	}
	
	@Bean
	Step importCompositesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importCompositesStep", jobRepository)
			.<Technique, Technique> chunk(10, transactionManager)
			.reader(reader())
			.processor(new TechniqueProcessor(TechniqueType.COMPOSITE))
			.writer(compositeWriter())
			.build();
	}
	
	@Bean
	Step importTechniquesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importTechniquesStep", jobRepository)
			.<Technique, Technique> chunk(10, transactionManager)
			.reader(reader())
			.processor(new TechniqueProcessor(TechniqueType.SINGLE))
			.writer(techniqueWriter())
			.build();
	}
	// end::jobstep[]
}