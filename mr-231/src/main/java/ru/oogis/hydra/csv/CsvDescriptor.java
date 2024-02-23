package ru.oogis.hydra.csv;

import org.supercsv.cellprocessor.ift.CellProcessor;

public interface CsvDescriptor
{
	Class<?> getBeanClass();
	String[] getFieldNames();
	CellProcessor[] getFieldProcessors();
	String getKeyFieldName();
}
