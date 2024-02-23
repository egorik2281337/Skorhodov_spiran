package ru.oogis.hydra.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigElementWithParameters extends ConfigElement
{

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	protected List<ConfigParameter> parameters;

	public ConfigElementWithParameters()
	{
		super();
		parameters = new ArrayList<ConfigParameter>();
	}

	public ConfigElementWithParameters(ConfigElementWithParameters p_value)
	{
		super(p_value);
		if (p_value != null)
		{
			parameters = new ArrayList<ConfigParameter>();
			copyParameters(p_value.getParameters());
		}
	}

	public ConfigElementWithParameters(String p_id, String p_displayName,
			String p_description)
	{
		super(p_id, p_displayName, p_description);
		parameters = new ArrayList<ConfigParameter>();
	}

	public ConfigParameter getParameter(String p_id)
	{
		if (p_id != null && parameters != null)
		{
			for (ConfigParameter a_parameter : parameters)
			{
				if (p_id.equals(a_parameter.getId()))
				{
					return a_parameter;
				}
			}
		}
		return null;
	}

	public List<ConfigParameter> getParameters()
	{
		return parameters;
	}

	public Properties getParametersAsProperties()
	{
		Properties a_result = new Properties();
		if (parameters != null)
		{
			for (ConfigParameter a_parameter : parameters)
			{
				a_result.setProperty(a_parameter.getId(), a_parameter.getValue().toString());
			}
		}
		return a_result;
	}

	public Object getParameterValue(String p_id)
	{
		ConfigParameter a_parameter = getParameter(p_id);
		if (a_parameter != null)
		{
			return a_parameter.getValue();
		}
		return null;
	}

	public <T> T getParameterValue(String p_id, Class<T> p_class)
	{
		ConfigParameter a_parameter = getParameter(p_id);
		if (a_parameter != null)
		{
			return a_parameter.convertValue(p_class);
		}
		return null;
	}

	public Map<String, Object> getParameterValueMap()
	{
		Map<String, Object> a_result = new HashMap<String, Object>();
		if (parameters != null)
		{
			for (ConfigParameter a_parameter : parameters)
			{
				a_result.put(a_parameter.getId(), a_parameter.getValue());
			}
		}
		return a_result;
	}

	public void setParameters(List<ConfigParameter> p_parameters)
	{
		parameters = p_parameters;
	}

	private void copyParameters(List<ConfigParameter> p_parameters)
	{
		if (p_parameters != null)
		{
			for (ConfigParameter a_parameter : p_parameters)
			{
				parameters.add(new ConfigParameter(a_parameter));
			}
		}
	}

}
