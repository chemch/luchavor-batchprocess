//package com.luchavor.batchprocess.processor;
//
//import org.springframework.batch.item.ItemProcessor;
//
//import com.luchavor.datamodel.event.connection.Connection;
//import com.luchavor.datamodel.event.connection.ConnectionAdapter;
//import com.luchavor.datamodel.event.connection.ConnectionImport;
//import com.luchavor.datamodel.util.converter.ConnectionConverter;
//
//import lombok.Data;
//
//@Data
//public class ConnectionProcessor implements ItemProcessor<ConnectionImport, Connection> {
//	
//	ConnectionConverter connectionConverter = new ConnectionConverter();
//	
//	@Override
//	public Connection process(final ConnectionImport connectionImport) throws Exception {		
//		ConnectionAdapter adapter = new ConnectionAdapter(connectionImport);
//		Connection converted = connectionConverter.toConnectionEvent(adapter);
//		return converted;
//	}
//}