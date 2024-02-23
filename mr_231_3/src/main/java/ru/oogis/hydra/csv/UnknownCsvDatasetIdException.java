package ru.oogis.hydra.csv;

import ru.oogis.hydra.util.MessageKey;
import ru.oogis.hydra.util.Messages;

import java.text.MessageFormat;

public class UnknownCsvDatasetIdException extends Exception
{

	private static final long serialVersionUID = 1L;

	public UnknownCsvDatasetIdException(String p_csvDatasetId)
	{
		super(createMeassage(p_csvDatasetId));
	}

	public UnknownCsvDatasetIdException(String p_csvDatasetId, Throwable p_cause)
	{
		super(createMeassage(p_csvDatasetId), p_cause);
	}

	public UnknownCsvDatasetIdException(String p_csvDatasetId, Throwable p_cause,
			boolean p_enableSuppression, boolean p_writableStackTrace)
	{
		super(createMeassage(p_csvDatasetId), p_cause, p_enableSuppression,
				p_writableStackTrace);
	}

	private static String createMeassage(String p_csvDatasetId)
	{
		return MessageFormat.format(
				Messages.getMessage(MessageKey.ERR_UNKNOWN_CSV_DATASET_ID),
				p_csvDatasetId);
	}

}
