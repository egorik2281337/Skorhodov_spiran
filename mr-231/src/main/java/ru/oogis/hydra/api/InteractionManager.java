package ru.oogis.hydra.api;

import ru.oogis.hydra.config.ChannelConfig;
import ru.oogis.hydra.config.ManagerConfig;
import ru.oogis.hydra.exception.ChannelNotFoundException;

import javax.ejb.Local;
import java.util.List;

@Local
public interface InteractionManager
{
	static final String CHANNEL_ID = "channel_id";
	static final String CONFIG_FILE_NAME = "config.xml";
	static final String CONFIG_ID = "config_id";
	static final String HYDRA_JNDI = "java:global/hydramanage/";

	void activateChannel(ChannelConfig p_channelConfig);

	void activateChannel(String p_channelId) throws ChannelNotFoundException;

	void addChannel(ChannelConfig p_channelConfig);

	void applyChannel(ChannelConfig p_channel);

	void applyConfiguration();

	ChannelConfig createChannel(ChannelConfig p_configTemplate);

	void deactivateChannel(ChannelConfig p_channelConfig);

	void deactivateChannel(String p_channelId) throws ChannelNotFoundException;

	ChannelConfig findChannel(String p_channelId) throws ChannelNotFoundException;

	List<ChannelConfig> getChannelTemplates();

	int getCleanInterval();

	ManagerConfig getConfiguration();

	String getId();

	int getMaxStorageTime();

	String getVersion();

	void logError(String p_channelId, Throwable p_ex);

	void removeChannel(ChannelConfig p_channelConfig);

	void removeChannel(String p_channelId) throws ChannelNotFoundException;

}
