package ru.oogis.hydra.validate.nmea;

import ru.oogis.hydra.validate.PayloadValidatorFactory;

public class NMEACRCValidatorFactory implements PayloadValidatorFactory<NMEACRCValidator, String>
{

	@Override
	public NMEACRCValidator createInstance(String p_payLoad)
	{
		return new NMEACRCValidator();
	}

}
