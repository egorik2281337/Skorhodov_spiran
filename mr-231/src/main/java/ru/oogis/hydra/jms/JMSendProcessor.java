package ru.oogis.hydra.jms;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.ServiceSupport;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class JMSendProcessor extends ServiceSupport implements Processor
{

	private static final String CONNECTION_FACTORY_NAME = "java:/ConnectionFactory";
	private String connectionFactoryName;
	private String destination;
	private Connection connection;
	private Session session;
	private MessageProducer producer;

	public JMSendProcessor(String p_destination)
	{
		super();
		destination = p_destination;
	}

	public String getConnectionFactoryName()
	{
		return connectionFactoryName;
	}

	@Override
	public void process(Exchange p_exchange) throws Exception
	{
		Object a_in = p_exchange.getIn().getBody();
		Message a_message = null;
		if (a_in instanceof byte[])
		{
			a_message = createBytesMessage((byte[]) a_in);
		}
		else if (a_in instanceof String)
		{
			a_message = session.createTextMessage(a_in.toString());
		}
		else if (a_in instanceof Serializable)
		{
			a_message = session.createObjectMessage((Serializable) a_in);
		}
		if (a_message != null)
		{
			Map<String, Object> a_headers = p_exchange.getIn().getHeaders();
			if (a_headers != null)
			{
				addMessageProperties(a_message, a_headers);
			}
			producer.send(a_message);
		}
		else
		{
			// TODO возможно поднять Exception
		}
	}

	private void addMessageProperties(Message p_message, Map<String, Object> p_headers)
			throws JMSException
	{
		Set<String> a_keys = p_headers.keySet();
		for (String a_key : a_keys)
		{
			p_message.setObjectProperty(a_key, p_headers.get(a_key));
		}
	}

	private Message createBytesMessage(byte[] p_body) throws JMSException
	{
		BytesMessage a_bytesMessage = session.createBytesMessage();
		a_bytesMessage.writeBytes(p_body);
		return a_bytesMessage;
	}

	public void setConnectionFactoryName(String p_connectionFactoryName)
	{
		connectionFactoryName = p_connectionFactoryName;
	}

	@Override
	protected void doStart() throws Exception
	{
		if (connectionFactoryName == null)
		{
			connectionFactoryName = CONNECTION_FACTORY_NAME;
		}
		InitialContext a_context = new InitialContext();
		try
		{
			ConnectionFactory a_factory =
					(ConnectionFactory) a_context.lookup(connectionFactoryName);
			Destination a_destination = (Destination) a_context.lookup(destination);
			connection = a_factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(a_destination);
			connection.start();
		}
		finally
		{
			a_context.close();
		}
	}

	@Override
	protected void doStop() throws Exception
	{
		try
		{
			if (producer != null)
			{
				producer.close();
			}
		}
		finally
		{
			try
			{
				if (session != null)
				{
					session.close();
				}
			}
			finally
			{
				if (connection != null)
				{
					connection.close();
				}
			}
		}
	}

}
