package io.github.swingboot.control;

import java.util.Optional;

import io.github.swingboot.control.annotation.InitializedBy;
import io.github.swingboot.control.parameter.ParameterSource;

class InitializedByDeclaration implements ControlDeclaration {

	private ParameterSourceContext parameterSourceContext;
	private ControlTypeInfo controlTypeInfo;

	public InitializedByDeclaration(InitializedBy annotation) {
		controlTypeInfo = ControlTypeInfo.of(annotation.value());
		parameterSourceContext = new ParameterSourceContext(controlTypeInfo, annotation.parameterSource(),
				this);
		parameterSourceContext.checkIfParameterSourceGivenWhenNonNullableParameter();
	}

	@Override
	public ControlTypeInfo getControlTypeInfo() {
		return controlTypeInfo;
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

}
