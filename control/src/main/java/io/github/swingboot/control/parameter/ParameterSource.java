package io.github.swingboot.control.parameter;

import java.util.EventObject;

public interface ParameterSource {

	String getId();

	Class<?> getValueReturnType();

	Object getValue(Object sourceOwner, EventObject event);
}
