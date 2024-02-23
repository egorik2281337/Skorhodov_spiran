package ru.oogis.hydra.csv;

import org.supercsv.cellprocessor.ift.CellProcessor;

public abstract class AbstractCsvDescriptor implements CsvDescriptor
{

	protected Class<?> beanClass;
	protected String[] fieldNames;
	protected CellProcessor[] fieldProcessors;
	protected String keyFieldName;
//	protected Properties contentReplacements;

	public AbstractCsvDescriptor(Class<?> p_beanClass, String p_keyFieldName)
	{
		super();
		beanClass = p_beanClass;
		keyFieldName = p_keyFieldName;
	}

	@Override
	public Class<?> getBeanClass()
	{
		return beanClass;
	}

	@Override
	public String[] getFieldNames()
	{
		if (fieldNames == null)
		{
			createFieldNames();
		}
		return fieldNames;
	}

	@Override
	public CellProcessor[] getFieldProcessors()
	{
		if (fieldProcessors == null)
		{
			createFieldProcessors();
		}
		return fieldProcessors;
	}

	@Override
	public String getKeyFieldName()
	{
		return keyFieldName;
	}

//	@Override
//	public String prepareContent(String p_content)
//	{
//		if (p_content != null)
//		{
//			if (contentReplacements == null)
//			{
//					initContentReplacements();
//			}
//			if (contentReplacements != null)
//			{
//				Set<String> a_propertyNames = contentReplacements.stringPropertyNames();
//				for (String a_key : a_propertyNames)
//				{
//					while (p_content.contains(a_key))
//					{
//						p_content =
//								p_content.replaceAll(Pattern.quote(a_key),
//										contentReplacements.getProperty(a_key));
//					}
//				}
//			}
//		}
//		return p_content;
//	}
	
	
//	protected abstract String getReplacementsPath();

	protected abstract void createFieldNames();

	protected abstract void createFieldProcessors();

//	private void initContentReplacements()
//	{
//		Properties a_properties = new Properties();
//	}

}
