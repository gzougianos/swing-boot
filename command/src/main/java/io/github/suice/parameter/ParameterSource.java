package io.github.suice.parameter;

import java.awt.AWTEvent;

public interface ParameterSource {

	String getId();

	Class<?> getValueReturnType();

	Object getValue(Object sourceOwner, AWTEvent event) throws ParameterSourceException;
}
