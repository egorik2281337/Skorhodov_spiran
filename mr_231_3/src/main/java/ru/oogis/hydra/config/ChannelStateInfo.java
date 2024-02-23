package ru.oogis.hydra.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChannelStateInfo
{
	private int errorCount;

	private long messageTime;

	private List<String> routesId;

	private long startTime;
	
	private boolean active;
	
	private String errorMessage;

	private final AtomicBoolean inWork = new AtomicBoolean(false);

	public ChannelStateInfo()
	{
		super();
		startTime = System.currentTimeMillis();
		messageTime = startTime;
		routesId = new ArrayList<String>();
	}

	public boolean isInWork()
	{
		return inWork.get();
	}

	public void fireRestart()
	{
		startTime = System.currentTimeMillis();
		messageTime = startTime;
	}

	public int getErrorCount()
	{
		return errorCount;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public long getMessageTime()
	{
		return messageTime;
	}

	public int getMessageTimeout()
	{
		return (int) ((System.currentTimeMillis() - messageTime) / 60000);
	}

	public List<String> getRoutesId()
	{
		return routesId;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public boolean isActive()
	{
		return active;
	}

	public void messageReceiving()
	{
		inWork.set(true);
	}
	
	public void messageReceived()
	{
		messageTime = System.currentTimeMillis();
		active = true;
		inWork.set(false);
	}

	public void setErrorMessage(String p_errorMessage)
	{
		errorMessage = p_errorMessage;
		errorCount++;
		active = false;
	}

}
