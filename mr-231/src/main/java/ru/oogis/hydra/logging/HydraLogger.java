package ru.oogis.hydra.logging;

import org.jboss.logging.Logger;

public class HydraLogger
{
	private static final String LOG_DELIMITER = ";";
	private final Logger log;

	public HydraLogger(String p_loggerName)
	{
		super();
		log = Logger.getLogger(p_loggerName);
	}

	public void logDebug(String p_configId, String p_channelId, String p_message)
	{
		if (log.isDebugEnabled())
		{
			log.debug(p_configId + LOG_DELIMITER + p_channelId + LOG_DELIMITER + p_message);
		}
	}

	public void logError(String p_configId, String p_channelId, String p_message,
			Throwable p_ex)
	{
		if (p_ex != null && log.isDebugEnabled())
		{
			log.error(p_configId + LOG_DELIMITER + p_channelId + LOG_DELIMITER + p_message,
					p_ex);
		}
		else
		{
			log.error(p_configId + LOG_DELIMITER + p_channelId + LOG_DELIMITER + p_message);
		}
	}

	public void logInfo(String p_configId, String p_channelId, String p_message)
	{
		if (log.isInfoEnabled())
		{
			log.info(p_configId + LOG_DELIMITER + p_channelId + LOG_DELIMITER + p_message);
		}
	}

	public void logTrace(String p_configId, String p_channelId, String p_message)
	{
		if (log.isTraceEnabled())
		{
			log.trace(p_configId + LOG_DELIMITER + p_channelId + LOG_DELIMITER + p_message);
		}
	}

	public void logWarn(String p_configId, String p_channelId, String p_message)
	{
		log.warn(p_configId + LOG_DELIMITER + p_channelId + LOG_DELIMITER + p_message);
	}

}
