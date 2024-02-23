package ru.oogis.hydra.config;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "iconfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class ManagerConfig extends ConfigElementWithParameters
{
	private String appUri;
	@XmlElementWrapper(name = "channels")
	@XmlElement(name = "channel")
	private List<ChannelConfig> channels;
	private int counter;
	@XmlTransient
	private String version;

	public ManagerConfig()
	{
		this(null, null, null);
	}

	public ManagerConfig(String p_id, String p_displayName, String p_description)
	{
		super(p_id, p_displayName, p_description);
	}

	public String getAppUri()
	{
		return appUri;
	}

	public List<ChannelConfig> getChannels()
	{
		return channels;
	}

	public int getCounter()
	{
		return counter++;
	}

	public String getVersion()
	{
		return version;
	}

	public void setAppUri(String p_appUri)
	{
		appUri = p_appUri;
	}

	public void setChannels(List<ChannelConfig> p_channels)
	{
		channels = p_channels;
	}

	public void setVersion(String p_version)
	{
		version = p_version;
	}

}
