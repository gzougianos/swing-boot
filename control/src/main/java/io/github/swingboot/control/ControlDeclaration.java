package io.github.swingboot.control;

import java.util.Optional;

import io.github.swingboot.control.parameter.ParameterSource;

public interface ControlDeclaration {

	ControlTypeInfo getControlTypeInfo();

	String getId();

	String getParameterSourceId();

	boolean expectsParameterSource();

	Optional<ParameterSource> getParameterSource();

	void setParameterSource(ParameterSource parameterSource);
}
