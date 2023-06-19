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

import com.luchavor.batchprocess.model.SingleTechnique;
import com.luchavor.batchprocess.processor.TechniqueProcessor;
import com.luchavor.batchprocess.writer.RestCallWriter;

@Configuration
public class BatchConfiguration {
	
	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	// tag::readerwriterprocessor[]
	@Bean
	FlatFileItemReader<SingleTechnique> reader() {
		return new FlatFileItemReaderBuilder<SingleTechnique>()
			.name("techniqueReader")
			.resource(new ClassPathResource("technique-data.csv"))
			.delimited()
			.names(new String[]{"matrixType", "mitreId", "tactic", "name", "description", "parentMitreId", "level"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<SingleTechnique>() {{ setTargetType(SingleTechnique.class); }})
			.linesToSkip(1) // skip top line which has headers
			.build();
	}
	
	@Bean
	RestCallWriter<SingleTechnique> writer() {
		return new RestCallWriter<SingleTechnique>();
	}

	@Bean
	TechniqueProcessor processor() {
		return new TechniqueProcessor();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	Job importTechniqueJob(JobRepository jobRepository, Step importSingleTechniqueStep) {
		return new JobBuilder("importTechniqueJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.flow(importSingleTechniqueStep)
			.end()
			.build();
	}

	@Bean
	Step importSingleTechniqueStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importSingleTechniqueStep", jobRepository)
			.<SingleTechnique, SingleTechnique> chunk(10, transactionManager)
			.reader(reader())
			.processor(processor())
			.writer(writer())
			.build();
	}
	// end::jobstep[]
}