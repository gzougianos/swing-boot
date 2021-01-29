package io.github.swingboot.control;

import java.util.Optional;

import io.github.swingboot.control.annotation.InitializedBy;
import io.github.swingboot.control.parameter.ParameterSource;

class InitializedByDeclaration implements ControlDeclaration {

	private final ParameterSourceContext parameterSourceContext;
	private final ControlTypeInfo controlTypeInfo;

	public InitializedByDeclaration(Class<?> annotatedClass, InitializedBy annotation) {
		controlTypeInfo = ControlTypeInfo.of(annotation.value());
		parameterSourceContext = new ParameterSourceContext(controlTypeInfo, annotation.parameterSource(),
				"@InitializedBy annotation in " + annotatedClass);
		parameterSourceContext.checkIfParameterSourceGivenWhenNonNullableParameter();
	}

	@Override
	public boolean expectsParameterSource() {
		return parameterSourceContext.expectsParameterSource();
	}

	@Override
	public void setParameterSource(ParameterSource parameterSource) {
		parameterSourceContext.setParameterSource(parameterSource);
	}

	@Override
	public Optional<ParameterSource> getParameterSource() {
		return parameterSourceContext.getParameterSource();
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getParameterSourceId() {
		return parameterSourceContext.getId();
	}

	@Override
	public Class<? extends Control<?>> getControlType() {
		return controlTypeInfo.getControlType();
	}

}
