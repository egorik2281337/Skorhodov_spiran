package ru.oogis.hydra.validate.nmea;

import ru.oogis.hydra.validate.PayloadValidatorFactory;

public abstract class NMEAValidatorFactory implements PayloadValidatorFactory<NMEAValidator, String>
{

	@Override
	public NMEAValidator createInstance(String p_payLoad)
	{
		return new NMEAValidator(getPattern(p_payLoad));
	}

	protected abstract String getPattern(String p_payLoad);

}
