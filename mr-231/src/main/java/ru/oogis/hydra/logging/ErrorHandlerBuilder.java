package ru.oogis.hydra.logging;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.util.CamelLogger;
import org.slf4j.LoggerFactory;

public class ErrorHandlerBuilder extends DefaultErrorHandlerBuilder
{

	@Override
	protected CamelLogger createLogger()
	{
		return new CamelLogger(LoggerFactory.getLogger("ru.oogis.hydra.Logger"),
		    LoggingLevel.ERROR);
	}

}
