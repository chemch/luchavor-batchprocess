//package com.luchavor.batchprocess.config.batch;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.transaction.PlatformTransactionManager;
//import com.luchavor.batchprocess.processor.ConnectionProcessor;
//import com.luchavor.batchprocess.processor.DnsEventProcessor;
//import com.luchavor.batchprocess.writer.EventRestApiWriter;
//import com.luchavor.datamodel.event.EventType;
//import com.luchavor.datamodel.event.connection.Connection;
//import com.luchavor.datamodel.event.connection.ConnectionImport;
//import com.luchavor.datamodel.event.dns.Dns;
//import com.luchavor.datamodel.event.dns.DnsEventImport;
//
//@Configuration
//public class EventImportConfig {
//
//	@Bean
//	FlatFileItemReader<ConnectionImport> connectionReader() {
//		return new FlatFileItemReaderBuilder<ConnectionImport>()
//			.name("connectionEventReader")
//			.resource(new ClassPathResource("input/conn.log"))
//			.lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {{
//                setNames(new String[]{"timestamp", "uid", "originatorIp", "originatorPort", "responderIp", "responderPort", "protocol", 
//                		"service", "duration", "originatorPayloadByteCount", "responderPayloadByteCount", "connectionState", "localOriginatorFlag", "localResponderFlag",
//                		"missedByteCount", "stateHistory", "originatorPacketCount", "originatorTotalByteCount", "responderPacketCount", "responderTotalByteCount", 
//                		"parentTunnelUid", "vlan", "innerVlan", "originatorMacAddress", "responderMacAddress", "speculativeService"});
//            }})
//			.fieldSetMapper(new BeanWrapperFieldSetMapper<ConnectionImport>() {{ setTargetType(ConnectionImport.class); }})
//			.build();
//	}
//	
//	@Bean
//	FlatFileItemReader<DnsEventImport> dnsEventReader() {
//		return new FlatFileItemReaderBuilder<DnsEventImport>()
//			.name("dnsEventReader")
//			.resource(new ClassPathResource("input/dns.log"))
//			.lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {{
//                setNames(new String[]{ 
//                		"timestamp",
//                		"uid",
//                		"originatorIp",
//                		"originatorPort",
//                		"responderIp",
//                		"responderPort",
//                		"protocol",
//                		"transactionId",
//                		"roundTripTime",
//                		"query",
//                		"qclass",
//                		"qclassName",
//                		"qtype",
//                		"qtypeName",
//                		"rcode",
//                		"rcodeName",
//                		"authoritativeAnswerFlag",
//                		"truncationFlag",
//                		"recursionDesiredFlag",
//                		"recursionAvailableFlag",
//                		"dnssecFlag",
//                		"answers",
//                		"ttls",
//                		"rejectedFlag",
//                		"authoritativeResponses",
//                		"additionalResponses",
//                		"originalQuery" });
//            }})
//			.fieldSetMapper(new BeanWrapperFieldSetMapper<DnsEventImport>() {{ setTargetType(DnsEventImport.class); }})
//			.build();
//	}
//
//	@Bean
//	Job importEventData(JobRepository jobRepository, Step importConnectionsStep, Step importDnsEventsStep) {
//		return new JobBuilder("importEventData", jobRepository)
//			.incrementer(new RunIdIncrementer())
//			.start(importConnectionsStep)
//			.next(importDnsEventsStep)
//			.build();
//	}
//	
//	@Bean
//	EventRestApiWriter<Connection> connectionWriter() {
//		return new EventRestApiWriter<Connection>(EventType.CONNECTION);
//	}
//	
//	@Bean
//	EventRestApiWriter<Dns> dnsEventWriter() {
//		return new EventRestApiWriter<Dns>(EventType.DNS);
//	}
//	
//	@Bean
//	ConnectionProcessor connectionProcessor() {
//		return new ConnectionProcessor();
//	}
//	
//	@Bean
//	DnsEventProcessor dnsEventProcessor() {
//		return new DnsEventProcessor();
//	}
//	
//	@Bean
//	Step importConnectionsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//		return new StepBuilder("importConnectionsStep", jobRepository)
//			.<ConnectionImport, Connection> chunk(10, transactionManager)
//			.reader(connectionReader())
//			.processor(connectionProcessor())
//			.writer(connectionWriter())
//			.build();
//	}
//	
//	@Bean
//	Step importDnsEventsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//		return new StepBuilder("importDnsEventsStep", jobRepository)
//			.<DnsEventImport, Dns> chunk(10, transactionManager)
//			.reader(dnsEventReader())
//			.processor(dnsEventProcessor())
//			.writer(dnsEventWriter())
//			.build();
//	}
//}