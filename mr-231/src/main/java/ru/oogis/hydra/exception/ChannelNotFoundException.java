package ru.oogis.hydra.exception;

public class ChannelNotFoundException extends HydraException
{

	private static final long serialVersionUID = 1L;

	public ChannelNotFoundException()
	{
		super();
	}

	public ChannelNotFoundException(String p_message, Throwable p_cause)
	{
		super(p_message, p_cause);
	}

	public ChannelNotFoundException(String p_message)
	{
		super(p_message);
	}

	public ChannelNotFoundException(Throwable p_cause)
	{
		super(p_cause);
	}

}
