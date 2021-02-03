package io.github.swingboot.control.declaration;

import java.util.Optional;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.parameter.ParameterSource;

public interface ControlDeclaration {

	Class<? extends Control<?>> getControlType();

	String getId();

	String getParameterSourceId();

	boolean expectsParameterSource();

	Optional<ParameterSource> getParameterSource();

	void setParameterSource(ParameterSource parameterSource);
}
