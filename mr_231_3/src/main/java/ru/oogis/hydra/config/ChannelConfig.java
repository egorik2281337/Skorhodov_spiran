package ru.oogis.hydra.config;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelConfig extends ConfigElementWithParameters
{
	@XmlElement(name = "activation-methods", defaultValue = "by_event")
	private ActivationMethod activationMethod = ActivationMethod.BY_EVENT;
	@XmlTransient
	private ChannelStateInfo channelStateInfo;
	private boolean enabled;
	@XmlTransient
	private long lastConnectTime;
	private long maxMessageTimeout;
	@XmlTransient
	private ManagerConfig owner;
	@XmlTransient
	private int reconnectAttempts;
	private int reconnectCount;
	private long reconnectTimeout = 60000l;
	private String resourceName;
	@XmlAttribute
	private int startupOrder;
	@XmlAttribute
	private long shutdownTimeout = 30000l;

	// @XmlTransient
	// private final AtomicBoolean inWork = new AtomicBoolean(false);

	public ChannelConfig()
	{
		super();
	}

	public ChannelConfig(ChannelConfig p_value)
	{
		super(p_value);
		if (p_value != null)
		{
			activationMethod = p_value.getActivationMethod();
			resourceName = p_value.getResourceName();
			startupOrder = p_value.getStartupOrder();
		}
	}

	public ChannelConfig(String p_id, String p_displayName, String p_description)
	{
		super(p_id, p_displayName, p_description);
	}

	public ActivationMethod getActivationMethod()
	{
		return activationMethod;
	}

	public ChannelState getChannelState()
	{
		if (!isEnabled())
		{
			return ChannelState.DISABLE;
		}
		if (isInWork())
		{
			return ChannelState.BUZY;
		}
		if (channelStateInfo == null || !channelStateInfo.isActive())
		{
			return ChannelState.INACTIVE;
		}
		if (maxMessageTimeout > 0)
		{
			long a_timeout =
					System.currentTimeMillis() - channelStateInfo.getMessageTime();
			if (a_timeout >= getMaxMessageTimeout() * 2)
			{
				return ChannelState.INACTIVE;
			}
			if (a_timeout >= getMaxMessageTimeout())
			{
				return ChannelState.EXPIRED;
			}
		}
		return ChannelState.ACTIVE;
	}

	public ChannelStateInfo getChannelStateInfo()
	{
		return channelStateInfo;
	}

	public long getLastConnectTime()
	{
		return lastConnectTime;
	}

	public long getMaxMessageTimeout()
	{
		return maxMessageTimeout;
	}

	public int getMaxTimeout()
	{
		return (int) (maxMessageTimeout / 60000);
	}

	public ManagerConfig getOwner()
	{
		return owner;
	}

	public int getReconnectAttempts()
	{
		return reconnectAttempts;
	}

	public int getReconnectCount()
	{
		return reconnectCount;
	}

	public int getReconnectInterval()
	{
		return (int) (reconnectTimeout / 60000);
	}

	public long getReconnectTimeout()
	{
		return reconnectTimeout;
	}

	public String getResourceName()
	{
		return resourceName;
	}

	public long getShutdownTimeout()
	{
		return shutdownTimeout;
	}

	public int getStartupOrder()
	{
		return startupOrder;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public boolean isInWork()
	{
		return channelStateInfo != null && channelStateInfo.isInWork();
		// return inWork.get();
	}

	public void setActivationMethod(ActivationMethod p_activationMethod)
	{
		activationMethod = p_activationMethod;
	}

	public void setChannelStateInfo(ChannelStateInfo p_channelStateInfo)
	{
		channelStateInfo = p_channelStateInfo;
	}

	public void setEnabled(boolean p_enabled)
	{
		enabled = p_enabled;
	}

	public void setLastConnectTime(long p_lastConnectTime)
	{
		lastConnectTime = p_lastConnectTime;
	}

	// public void setInWork(boolean p_inWork)
	// {
	// inWork.set(p_inWork);
	// }

	public void setMaxMessageTimeout(long p_maxMessageTimeout)
	{
		maxMessageTimeout = p_maxMessageTimeout;
	}

	public void setMaxTimeout(int p_maxTimeout)
	{
		maxMessageTimeout = p_maxTimeout * 60000l;
	}

	public void setOwner(ManagerConfig p_owner)
	{
		owner = p_owner;
	}

	public void setReconnectAttempts(int p_reconnectAttempts)
	{
		reconnectAttempts = p_reconnectAttempts;
	}

	public void setReconnectCount(int p_reconnectCount)
	{
		reconnectCount = p_reconnectCount;
	}

	public void setReconnectInterval(int p_interval)
	{
		reconnectTimeout = p_interval * 60000l;
	}

	public void setReconnectTimeout(long p_reconnectTimeout)
	{
		reconnectTimeout = p_reconnectTimeout;
	}

	public void setResourceName(String p_resourceName)
	{
		resourceName = p_resourceName;
	}

	public void setShutdownTimeout(long p_shutdownTimeout)
	{
		shutdownTimeout = p_shutdownTimeout;
	}

	public void setStartupOrder(int p_startupOrder)
	{
		startupOrder = p_startupOrder;
	}

}
