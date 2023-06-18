package com.luchavor.neo4jbatch;

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

@Configuration
public class BatchConfiguration {

	// tag::readerwriterprocessor[]
	@Bean
	FlatFileItemReader<SingleTechnique> reader() {
		return new FlatFileItemReaderBuilder<SingleTechnique>()
			.name("techniqueReader")
			.resource(new ClassPathResource("technique-data.csv"))
			.delimited()
			.names(new String[]{"mitreId", "tactic", "name", "description"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<SingleTechnique>() {{ setTargetType(SingleTechnique.class); }})
			.build();
	}

	@Bean
	TechniqueProcessor processor() {
		return new TechniqueProcessor();
	}
	
	@Bean 
	NoOpItemWriter<SingleTechnique> writer() {
		return new NoOpItemWriter<SingleTechnique>();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	Job importUserJob(JobRepository jobRepository, Step step1) {
		return new JobBuilder("importUserJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("step1", jobRepository)
			.<SingleTechnique, SingleTechnique> chunk(10, transactionManager)
			.reader(reader())
			.processor(processor())
			.writer(writer())
			.build();
	}
	// end::jobstep[]
}