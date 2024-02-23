package ru.oogis.hydra.config;

import javax.xml.bind.annotation.*;
import java.text.MessageFormat;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "http://www.w3.org/2001/XMLSchema")
public class ConfigParameter extends ConfigElement
{
	private boolean secret;
	private String uom;
	@XmlElementWrapper(name = "valueItems")
	@XmlElement(name = "valueItem")
	private List<String> valueItems;
	@XmlElement(namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private Object value;

	public ConfigParameter()
	{
		super();
	}

	public ConfigParameter(ConfigParameter p_parameter)
	{
		super(p_parameter);
		if (p_parameter != null)
		{
			init(p_parameter.getValue(), p_parameter.getUom());
			secret = p_parameter.isSecret();
      valueItems = p_parameter.getValueItems();
		}
	}

	public ConfigParameter(String p_id, String p_displayName, Object p_value,
			String p_uom, String p_description)
	{
		super(p_id, p_displayName, p_description);
		init(p_value, p_uom);
	}

	public static ConfigParameter generateTemplate()
	{
		return new ConfigParameter("a_id", "a_displayName", "a_value", "a_uom",
				"a_description");
	}

	public <T> T convertValue(Class<T> p_class)
	{
		return convertValue(value, p_class);
	}

	public ConfigParameter copy()
	{
		ConfigParameter a_result = new ConfigParameter();
		a_result.setId(id);
		a_result.setDisplayName(displayName);
		a_result.setDescription(description);
		a_result.setValue(value);
		a_result.setUom(uom);
		return a_result;
	}

	public String getUom()
	{
		return uom;
	}

	public Object getValue()
	{
		return value;
	}

	public List<String> getValueItems()
	{
		return valueItems;
	}

	public boolean isLogical()
	{
		return value instanceof Boolean;
	}

	public boolean isSecret()
	{
		return secret;
	}

	public boolean isSelectable()
	{
		return valueItems != null && valueItems.size() > 0;
	}

	public void setSecret(boolean p_secret)
	{
		secret = p_secret;
	}

	public void setUom(String p_uom)
	{
		uom = p_uom;
	}

	public void setValue(Object p_value)
	{
		if (value == null || p_value == null)
		{
			value = p_value;
		}
		else if (value != null && p_value != null)
		{
			value = convertValue(p_value, value.getClass());
		}
	}

	public void setValueItems(List<String> p_valueItems)
	{
		valueItems = p_valueItems;
	}

	private <T> T convertValue(Object p_value, Class<T> p_class)
	{
		if (p_value != null && p_class != null)
		{
			return p_class.cast(p_value);
		}
		throw new IllegalArgumentException(MessageFormat.format(
				"Can't convert value {0} to {1})", value, p_class));
	}

	private void init(Object p_value, String p_uom)
	{
		value = p_value;
		uom = p_uom;
	}

}
