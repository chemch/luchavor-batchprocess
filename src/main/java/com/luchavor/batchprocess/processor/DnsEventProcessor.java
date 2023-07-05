package com.luchavor.batchprocess.processor;

import org.springframework.batch.item.ItemProcessor;
import com.luchavor.datamodel.event.dns.Dns;
import com.luchavor.datamodel.event.dns.DnsEventAdapter;
import com.luchavor.datamodel.event.dns.DnsEventImport;
import com.luchavor.datamodel.util.converter.DnsEventConverter;

import lombok.Data;

@Data
public class DnsEventProcessor implements ItemProcessor<DnsEventImport, Dns> {
	
	DnsEventConverter dnsEventConverter = new DnsEventConverter();
	
	@Override
	public Dns process(final DnsEventImport dnsEventImport) throws Exception {		
		DnsEventAdapter adapter = new DnsEventAdapter(dnsEventImport);
		Dns converted = dnsEventConverter.toDnsEvent(adapter);
		return converted;
	}
}