package ru.oogis.hydra.api;

import org.apache.camel.Exchange;

import java.util.List;


public interface ExchangeConverter<T>
{
	List<T> convert(Exchange p_exchange) throws Exception;
}
