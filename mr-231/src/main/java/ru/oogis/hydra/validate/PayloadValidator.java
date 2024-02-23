package ru.oogis.hydra.validate;

public interface PayloadValidator<T>
{
	boolean validate(T p_payload);
}
