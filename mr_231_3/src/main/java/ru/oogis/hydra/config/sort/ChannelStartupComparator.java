package ru.oogis.hydra.config.sort;

import ru.oogis.hydra.config.ChannelConfig;

public class ChannelStartupComparator extends
		AbstractConfigElementComparator<ChannelConfig>
{

	@Override
	protected int compareConfig(ChannelConfig p_config1, ChannelConfig p_config2)
	{
		return Integer.compare(p_config1.getStartupOrder(),
				p_config2.getStartupOrder());
	}
}
