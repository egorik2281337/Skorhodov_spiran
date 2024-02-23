package ru.oogis.hydra.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="activation-method-type")
@XmlEnum
public enum ActivationMethod
{
	@XmlEnumValue("by_event")
	BY_EVENT,
	@XmlEnumValue("scheduled")
	SCHEDULED
}
