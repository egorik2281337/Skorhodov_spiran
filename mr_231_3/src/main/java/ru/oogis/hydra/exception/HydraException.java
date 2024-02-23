package ru.oogis.hydra.exception;

public class HydraException extends Exception
{

	private static final long serialVersionUID = 1L;

	public HydraException()
	{
		super();
	}

	public HydraException(String p_message)
	{
		super(p_message);
	}

	public HydraException(Throwable p_cause)
	{
		super(p_cause);
	}

	public HydraException(String p_message, Throwable p_cause)
	{
		super(p_message, p_cause);
	}

}
