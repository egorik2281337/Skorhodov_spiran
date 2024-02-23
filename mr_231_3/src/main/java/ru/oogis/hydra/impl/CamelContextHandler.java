package ru.oogis.hydra.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.logging.Logger;
import ru.oogis.hydra.logging.HydraLogger;

import javax.naming.InitialContext;

public final class CamelContextHandler
{

	private static final String CONTEXT_NAME = "hydra-context";
	private static final String DLC_SUFFIX = "-deadletter";

	private static CamelContextHandler handler;

	private DefaultCamelContext camelContext;
	private final HydraLogger hydraLogger;
  private final Logger log = Logger.getLogger(CamelContextHandler.class);

	private CamelContextHandler() throws Exception
	{
		super();
		hydraLogger = new HydraLogger("ru.oogis.hydra");
		camelContext = new DefaultCamelContext();
		camelContext.setName(CONTEXT_NAME);
		camelContext.getShutdownStrategy().setShutdownNowOnTimeout(true);
		camelContext.getShutdownStrategy().setTimeout(5);
		InitialContext a_context = new InitialContext();
		a_context.bind(CONTEXT_NAME + DLC_SUFFIX, new ChannelDeadLetterHandler());
		DeadLetterChannelBuilder a_errorHandlerBuilder =
				new DeadLetterChannelBuilder("bean:" + CONTEXT_NAME + DLC_SUFFIX);
		a_errorHandlerBuilder.allowRedeliveryWhileStopping(false);
		// a_errorHandlerBuilder.loggingLevel(LoggingLevel.TRACE);
		// a_errorHandlerBuilder.disableRedelivery();
		camelContext.setErrorHandlerBuilder(a_errorHandlerBuilder);
	}

	public static synchronized CamelContext getContext() throws Exception
	{
		if (handler == null)
		{
			handler = new CamelContextHandler();
			handler.camelContext.start();
		}
		return handler.camelContext;
	}

	public class ChannelDeadLetterHandler implements Processor {
		@Override
		public void process(Exchange p_exchange) throws Exception {
			String a_channelId = (String) p_exchange.getIn().getHeader("channel_id");
			if (a_channelId.contains(".")) {
				String a_managerId = a_channelId.substring(0, a_channelId.indexOf("."));
				Throwable a_throwable = p_exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
				System.out.println(a_throwable.getClass().getName());
				hydraLogger.logError(a_managerId, a_channelId, a_throwable.getMessage(), a_throwable);
			} else throw new StringIndexOutOfBoundsException(-1);
		}
	}

}
