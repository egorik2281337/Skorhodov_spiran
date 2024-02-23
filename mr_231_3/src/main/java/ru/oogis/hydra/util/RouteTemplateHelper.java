package ru.oogis.hydra.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RouteTemplateHelper
{
	public List<String> getParameterIds(String p_routesXml)
	{
		List<String> a_result = new ArrayList<String>();
		if (p_routesXml != null)
		{
			String[] a_params = p_routesXml.split("[$]");
			for (int i = 0; i < a_params.length; i++)
			{
				String a_param = a_params[i];
				if (a_param.startsWith("{"))
				{
					int a_index = a_param.indexOf("}");
					if(a_index == -1)
					{
						throw new RuntimeException("Missing '}' in parameter definition: "+a_param);
					}
					a_param = a_param.substring(1, a_index);
					a_result.add(a_param);
				}
			}
		}
		return a_result;
	}

	public String loadRoutesFromFile(File p_file) throws IOException
	{
		if (p_file != null)
		{
			return loadRoutes(new FileInputStream(p_file));
		}
		return "";
	}

	public String loadRoutesFromResources(Class<?> p_class, String p_resourcePath)
			throws IOException
	{
		if (p_class != null && p_resourcePath != null)
		{
			return loadRoutes(p_class.getResourceAsStream(p_resourcePath));
		}
		return "";
	}

	private String loadRoutes(InputStream p_stream) throws IOException
	{
		try
		{
			byte[] a_buffer = new byte[p_stream.available()];
			p_stream.read(a_buffer);
			return new String(a_buffer);
		}
		finally
		{
			p_stream.close();
		}
	}
}
