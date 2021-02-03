package io.github.swingboot.control.declaration;

import static io.github.swingboot.control.reflect.ReflectionUtils.equalsOrExtends;

import java.util.Optional;

import io.github.swingboot.control.parameter.InvalidParameterSourceException;
import io.github.swingboot.control.parameter.ParameterSource;

class ParameterSourceContext {

	private final String parameterSourceId;
	private final ControlTypeInfo controlTypeInfo;
	private ParameterSource parameterSource;
	private final Object objectToPrintOnError;

	ParameterSourceContext(ControlTypeInfo controlTypeInfo, String parameterSourceId,
			Object objectToPrintOnError) {
		this.controlTypeInfo = controlTypeInfo;
		this.parameterSourceId = parameterSourceId;
		this.objectToPrintOnError = objectToPrintOnError;

	}

	void checkIfParameterSourceGivenWhenNonNullableParameter() {
		if (controlTypeInfo.isParameterless())
			return;

		if (!controlTypeInfo.isParameterNullable() && parameterSourceId.isEmpty()) {
			String format = "%s declares a parameterized control with non-nullable %s parameter but no parameter source was declared.";
			throw new InvalidParameterSourceException(String.format(format, objectToPrintOnError.toString(),
					controlTypeInfo.getParameterType().getSimpleName()));
		}
	}

	public String getId() {
		return parameterSourceId;
	}

	void setParameterSource(ParameterSource parameterSource) {
		if (controlTypeInfo.isParameterless()) {
			String msg = "%s declares parameter source for parameterless declared control %s.";
			msg = String.format(msg, objectToPrintOnError.toString(), controlTypeInfo.getControlType());
			throw new InvalidParameterSourceException(msg);
		}

		if (!parameterSource.getId().equals(parameterSourceId))
			throw new InvalidParameterSourceException(objectToPrintOnError
					+ " does not a support a parameter source with id " + parameterSource.getId() + "");

		checkIfParameterSourceReturnTypeMatchesControlParameterType(parameterSource);
		this.parameterSource = parameterSource;
	}

	private void checkIfParameterSourceReturnTypeMatchesControlParameterType(
			ParameterSource parameterSource) {
		Class<?> parameterSourceReturnType = parameterSource.getValueReturnType();

		if (!equalsOrExtends(parameterSourceReturnType, controlTypeInfo.getParameterType())) {
			throw new InvalidParameterSourceException(
					objectToPrintOnError + " declares a parameter source that returns "
							+ parameterSourceReturnType.getSimpleName() + " values while control takes "
							+ controlTypeInfo.getParameterType().getSimpleName() + " parameter.");
		}
	}

	public boolean expectsParameterSource() {
		return !parameterSourceId.isEmpty() && !controlTypeInfo.isParameterless();
	}

	public Optional<ParameterSource> getParameterSource() {
		return Optional.ofNullable(parameterSource);
	}

}
