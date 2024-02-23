package ru.oogis.hydra.config.sort;

import ru.oogis.hydra.config.ConfigElement;

import java.util.Comparator;

public abstract class AbstractConfigElementComparator<T extends ConfigElement> implements Comparator<T>
{

	public AbstractConfigElementComparator()
	{
		super();
	}

	@Override
	public int compare(T p_config1, T p_config2)
	{
		Integer a_validationResult = validateParameters(p_config1, p_config2);
		if(a_validationResult != null)
		{
			return a_validationResult;
		}
		return compareConfig(p_config1, p_config2);
	}

	protected abstract int compareConfig(T p_config1, T p_config2);

	private Integer validateParameters(T p_config1, T p_config2)
	{
		if (p_config1 == null && p_config2 == null)
		{
			return 0;
		}
		if(p_config1 == null)
		{
			return -1;
		}
		if(p_config2 == null)
		{
			return 1;
		}
		return null;
	}

}