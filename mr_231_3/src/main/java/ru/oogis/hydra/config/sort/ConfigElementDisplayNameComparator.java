package ru.oogis.hydra.config.sort;

import ru.oogis.hydra.config.ConfigElement;

import java.util.Objects;

public class ConfigElementDisplayNameComparator<T extends ConfigElement> extends AbstractConfigElementComparator<T>
{

	@Override
	protected int compareConfig(T p_config1, T p_config2)
	{
		String a_name1 = Objects.toString(p_config1.getDisplayName(), "");
		String a_name2 = Objects.toString(p_config2.getDisplayName(), "");
		return a_name1.compareTo(a_name2);
	}

}
