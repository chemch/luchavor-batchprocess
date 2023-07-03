package com.luchavor.batchprocess.processor;

import org.springframework.batch.item.ItemProcessor;
import com.luchavor.datamodel.event.EventType;
import com.luchavor.datamodel.event.connection.ConnectionEvent;
import com.luchavor.datamodel.event.connection.ConnectionEventAdapter;
import com.luchavor.datamodel.event.connection.ConnectionEventImport;
import com.luchavor.datamodel.util.converter.ConnectionEventConverter;

import lombok.Data;

@Data
public class EventProcessor implements ItemProcessor<ConnectionEventImport, ConnectionEvent> {
	
	ConnectionEventConverter connectionEventConverter = new ConnectionEventConverter();
	
	private final EventType eventType;
	
	@Override
	public ConnectionEvent process(final ConnectionEventImport connectionEventImport) throws Exception {		
		ConnectionEventAdapter adapter = new ConnectionEventAdapter(connectionEventImport);
		ConnectionEvent convertedConnectionEvent = connectionEventConverter.toConnectionEvent(adapter);
		return convertedConnectionEvent;
	}
}