package ru.oogis.hydra.logging;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.sql.DataSource;

public class EventLogCleaner implements Processor
{
	private DataSource dataSource;
	
	
	public EventLogCleaner(DataSource p_dataSource)
	{
		dataSource = p_dataSource;
	}


	@Override
	public void process(Exchange p_exchange) throws Exception
	{
		if (dataSource != null)
		{
			
		}
	}

}
