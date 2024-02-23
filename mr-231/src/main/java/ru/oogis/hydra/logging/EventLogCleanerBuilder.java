package ru.oogis.hydra.logging;

import org.apache.camel.builder.RouteBuilder;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class EventLogCleanerBuilder extends RouteBuilder
{

	@Override
	public void configure() throws Exception
	{
		InitialContext a_context = new InitialContext();
		Object a_object = a_context.lookup("java:jboss/datasources/hydra-ds");
		if (a_object instanceof DataSource)
		{
			DataSource a_dataSource = (DataSource) a_object;
			EventLogCleaner a_cleaner = new EventLogCleaner(a_dataSource);
			from("timer://hydraEventLogCleaner?fixedRate=true&delay=120000&period=300000").process(a_cleaner);
		}
	}

}
