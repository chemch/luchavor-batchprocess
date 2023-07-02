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
public class EventImportConfig {
	
//	@Bean
//	RestTemplate restTemplate(RestTemplateBuilder builder) {
//		return builder.build();
//	}
//	
	// tag::importZeekEventsJob[]
//	@Bean
//	FlatFileItemReader<Event> zeekLogReader() {
//		return new FlatFileItemReaderBuilder<Event>()
//			.name("zeekEventReader")
//			.resource(new ClassPathResource("conn.log"))
//			.lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {{
//                setNames(new String[]{"timestamp", "uniqueId"});
//            }})
//			.fieldSetMapper(new BeanWrapperFieldSetMapper<Event>() {{ setTargetType(Event.class); }})
//			.linesToSkip(1) // skip top line which has headers
//			.build();
//	}
//	
//	@Bean
//	RestApiWriter<Event> zeekEventWriter() {
//		return new RestApiWriter<Event>(TechniqueType.COMPOSITE);
//	}
//	
//	@Bean
//	Job importZeekEventsJob(JobRepository jobRepository, Step importConnLogStep) {
//		return new JobBuilder("importZeekEventsJob", jobRepository)
//			.incrementer(new RunIdIncrementer())
//			.start(importConnLogStep)
//			.build();
//	}
//	
	// import conn log events
//	@Bean
//	Step importConnLogStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//		return new StepBuilder("importConnLogStep", jobRepository)
//			.<Event, Event> chunk(10, transactionManager)
//			.reader(zeekLogReader())
//			.writer(techniqueWriter())
//			.build();
//	}
	// end::importZeekEventsJob[]
}