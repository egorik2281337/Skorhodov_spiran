package ru.oogis.hydra.validate.nmea;

import java.util.regex.Pattern;

public class NMEAValidator extends NMEACRCValidator
{

	private String pattern;
	
	public NMEAValidator(String p_pattern)
	{
		super();
		pattern = p_pattern;
	}

	@Override
	public boolean validate(String p_payload)
	{
		if (!isCRCValid(p_payload))
		{
			return false;
		}
		Pattern a_pattern = Pattern.compile(pattern);
		return a_pattern.matcher(p_payload).matches();
	}


}
