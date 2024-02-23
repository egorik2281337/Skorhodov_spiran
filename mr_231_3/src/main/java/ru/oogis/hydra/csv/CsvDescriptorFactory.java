package ru.oogis.hydra.csv;

public interface CsvDescriptorFactory
{
	CsvDescriptor create(String p_csvDatasetId) throws Exception;
}
