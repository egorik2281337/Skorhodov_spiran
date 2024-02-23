package ru.oogis.hydra.validate;

public interface PayloadValidatorFactory<E extends PayloadValidator<T>, T>
{
	E createInstance(T p_payLoad);
}
