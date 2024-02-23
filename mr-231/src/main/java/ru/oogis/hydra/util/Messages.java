package ru.oogis.hydra.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
	private static final String MESSAGE_BUNDLE_NAME = "ru.oogis.hydra.util.messages"; //$NON-NLS-1$
	private static final String RESOURCE_BUNDLE_NAME = "ru.oogis.hydra.util.resources"; //$NON-NLS-1$

	private static final ResourceBundle MESSAGE_BUNDLE = ResourceBundle
	    .getBundle(MESSAGE_BUNDLE_NAME);

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(RESOURCE_BUNDLE_NAME); //$NON-NLS-1$

	private Messages()
	{
	}
	public static String getMessage(MessageKey p_key)
	{
		return getMessage(p_key.name());
	}
	
	public static String getMessage(String p_key)
	{
		try
		{
			return MESSAGE_BUNDLE.getString(p_key);
		}
		catch (MissingResourceException e)
		{
			return '!' + p_key + '!';
		}
	}

	public static String getResource(String p_key, String p_default)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(p_key);
		}
		catch (MissingResourceException e)
		{
			return p_default;
		}
	}
}
