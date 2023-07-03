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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import com.luchavor.batchprocess.processor.EventProcessor;
import com.luchavor.batchprocess.writer.EventRestApiWriter;
import com.luchavor.datamodel.event.EventType;
import com.luchavor.datamodel.event.connection.Connection;
import com.luchavor.datamodel.event.connection.ConnectionEvent;
import com.luchavor.datamodel.event.connection.ConnectionEventImport;

@Configuration
public class EventImportConfig {

	@Bean
	FlatFileItemReader<ConnectionEventImport> eventReader() {
		return new FlatFileItemReaderBuilder<ConnectionEventImport>()
			.name("eventReader")
			.resource(new ClassPathResource("input/conn.log"))
			.lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {{
                setNames(new String[]{"timestamp", "uid", "originatorIp", "originatorPort", "responderIp", "responderPort", "protocol", 
                		"service", "duration", "originatorPayloadByteCount", "responderPayloadByteCount", "connectionState", "localOriginatorFlag", "localResponderFlag",
                		"missedByteCount", "stateHistory", "originatorPacketCount", "originatorTotalByteCount", "responderPacketCount", "responderTotalByteCount", 
                		"parentTunnelUid", "vlan", "innerVlan", "originatorMacAddress", "responderMacAddress"});
            }})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<ConnectionEventImport>() {{ setTargetType(ConnectionEventImport.class); }})
			.build();
	}

	@Bean
	Job importZeekEventData(JobRepository jobRepository, Step importConnectionEventsStep) {
		return new JobBuilder("importZeekEventData", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(importConnectionEventsStep)
			.build();
	}
	
	@Bean
	EventRestApiWriter<ConnectionEvent> eventRestApiWriter() {
		return new EventRestApiWriter<ConnectionEvent>(EventType.CONNECTION);
	}
	
	// import conn log events
	@Bean
	Step importConnectionEventsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("importConnectionEventsStep", jobRepository)
			.<ConnectionEventImport, Connection> chunk(1, transactionManager)
			.reader(eventReader())
			.processor(new EventProcessor(EventType.CONNECTION))
			.writer(eventRestApiWriter())
			.build();
	}
}