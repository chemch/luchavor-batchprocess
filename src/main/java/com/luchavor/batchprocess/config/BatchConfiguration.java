package com.luchavor.batchprocess.config;

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
import com.luchavor.batchprocess.listener.ExecutionListener;
import com.luchavor.batchprocess.processor.TechniqueProcessor;
import com.luchavor.batchprocess.writer.RestApiWriter;
import com.luchavor.datamodel.event.Event;
import com.luchavor.datamodel.technique.ImportTechniqueItem;
import com.luchavor.datamodel.technique.TechniqueType;

@Configuration
public class BatchConfiguration {
	
	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	// tag::readerwriterprocessor[]
	@Bean
	FlatFileItemReader<ImportTechniqueItem> reader() {
		return new FlatFileItemReaderBuilder<ImportTechniqueItem>()
			.name("techniqueReader")
			.resource(new ClassPathResource("data/technique-data.csv"))
			.delimited()
			.names(new String[]{"model", "subModel", "mitreId", "tactic", "name", "description", "parentMitreId", "treeLevel", "type"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<ImportTechniqueItem>() {{ setTargetType(ImportTechniqueItem.class); }})
			.linesToSkip(1) // skip top line which has headers
			.build();
	}
	
	@Bean
	FlatFileItemReader<Event> zeekLogReader() {
		return new FlatFileItemReaderBuilder<Event>()
			.name("zeekEventReader")
			.resource(new ClassPathResource("conn.log"))
			.lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {{
                setNames(new String[]{"timestamp", "uniqueId"});
            }})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Event>() {{ setTargetType(Event.class); }})
			.linesToSkip(1) // skip top line which has headers
			.build();
	}
	
	@Bean
	RestApiWriter<ImportTechniqueItem> techniqueWriter() {
		return new RestApiWriter<ImportTechniqueItem>(TechniqueType.SINGLE);
	}
	
	@Bean
	RestApiWriter<ImportTechniqueItem> compositeWriter() {
		return new RestApiWriter<ImportTechniqueItem>(TechniqueType.COMPOSITE);
	}
	
	@Bean
	RestApiWriter<Event> zeekEventWriter() {
		return new RestApiWriter<Event>(TechniqueType.COMPOSITE);
	}
	// end::readerwriterprocessor[]

	// tag::importTechniquesJob[]
	@Bean
	Job importTechniqueJob(JobRepository jobRepository, ExecutionListener executionListener, Step importTechniquesStep, Step importCompositesStep) {
		return new JobBuilder("importTechniqueJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(executionListener)
			.start(importTechniquesStep)
			.next(importCompositesStep)
			.build();
	}
	
	// import single techniques
	@Bean
	Step importTechniquesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importTechniquesStep", jobRepository)
			.<ImportTechniqueItem, ImportTechniqueItem> chunk(100, transactionManager)
			.reader(reader())
			.processor(new TechniqueProcessor(TechniqueType.SINGLE))
			.writer(techniqueWriter())
			.build();
	}
	
	// import composite techniques
	@Bean
	Step importCompositesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importCompositesStep", jobRepository)
			.<ImportTechniqueItem, ImportTechniqueItem> chunk(200, transactionManager)
			.reader(reader())
			.processor(new TechniqueProcessor(TechniqueType.COMPOSITE))
			.writer(compositeWriter())
			.build();
	}
	// end::importTechniquesJob[]
	
	// tag::importZeekEventsJob[]
//	@Bean
//	Job importZeekEventsJob(JobRepository jobRepository, Step importConnLogStep) {
//		return new JobBuilder("importZeekEventsJob", jobRepository)
//			.incrementer(new RunIdIncrementer())
//			.start(importConnLogStep)
//			.build();
//	}
//	
//	// import conn log events
//	@Bean
//	Step importConnLogStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//		return new StepBuilder("importConnLogStep", jobRepository)
//			.<ZeekEvent, ZeekEvent> chunk(10, transactionManager)
//			.reader(zeekLogReader())
//			.writer(techniqueWriter())
//			.build();
//	}
	// end::importZeekEventsJob[]
}