package ru.oogis.hydra.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigElement
{
	@XmlElement
	protected String description;
	@XmlElement
	protected String displayName;
	@XmlAttribute
	protected String id;

	public ConfigElement()
	{
		super();
	}

	public ConfigElement(ConfigElement p_value)
	{
		super();
		if (p_value != null)
		{
			init(p_value.getId(), p_value.getDisplayName(), p_value.getDescription());
		}
	}

	public ConfigElement(String p_id, String p_displayName, String p_description)
	{
		super();
		init(p_id, p_displayName, p_description);
	}

	@Override
	public boolean equals(Object p_value)
	{
		if (this == p_value)
			return true;
		if (p_value == null)
			return false;
		if (getClass() != p_value.getClass())
			return false;
		ConfigElement other = (ConfigElement) p_value;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (displayName == null)
		{
			if (other.displayName != null)
				return false;
		}
		else if (!displayName.equals(other.displayName))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getDescription()
	{
		return description;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public String getId()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setDescription(String p_description)
	{
		description = p_description;
	}

	public void setDisplayName(String p_displayName)
	{
		displayName = p_displayName;
	}

	public void setId(String p_id)
	{
		id = p_id;
	}

	private void init(String p_id, String p_displayName, String p_description)
	{
		id = p_id;
		displayName = p_displayName;
		description = p_description;
	}

}
