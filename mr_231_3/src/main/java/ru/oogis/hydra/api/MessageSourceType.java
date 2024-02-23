package ru.oogis.hydra.api;

public interface MessageSourceType<T>
{
	String getTypeName();
	
	ExchangeConverter<T> getConverter();

}
