package ru.oogis.hydra.config.sort;

import ru.oogis.hydra.config.ChannelConfig;
import ru.oogis.hydra.config.ManagerConfig;

import java.util.Objects;

public class ChannelConfigDisplayNameComparator extends ConfigElementDisplayNameComparator<ChannelConfig>
{

	@Override
	protected int compareConfig(ChannelConfig p_config1, ChannelConfig p_config2)
	{
		ManagerConfig a_config1 = p_config1.getOwner(); 
		ManagerConfig a_config2 = p_config2.getOwner();
		String a_name1 = a_config1 == null ? "" : Objects.toString(a_config1.getDisplayName(), "");
		String a_name2 = a_config2 == null ? "" : Objects.toString(a_config2.getDisplayName(), "");
		String a_channelName1 = Objects.toString(p_config1.getDisplayName(), "");
		String a_channelName2 = Objects.toString(p_config2.getDisplayName(), "");
		return (a_name1+a_channelName1).compareTo(a_name2+a_channelName2);
	}

}
