package io.github.suice.control.parameter;

import java.awt.AWTEvent;

public interface ParameterSource {

	String getId();

	Class<?> getValueReturnType();

	Object getValue(Object sourceOwner, AWTEvent event) throws InvalidParameterSourceException;
}
