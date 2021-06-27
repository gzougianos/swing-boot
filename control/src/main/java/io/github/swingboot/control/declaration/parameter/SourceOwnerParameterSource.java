package io.github.swingboot.control.declaration.parameter;

import static io.github.swingboot.control.ParameterSource.THIS;

import java.util.EventObject;

public class SourceOwnerParameterSource implements ParameterSource {
	private Class<?> clazz;

	public SourceOwnerParameterSource(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public String getId() {
		return THIS;
	}

	@Override
	public Class<?> getValueReturnType() {
		return clazz;
	}

	@Override
	public Object getValue(Object sourceOwner, EventObject event) {
		return sourceOwner;
	}

	@Override
	public String toString() {
		return "SourceOwnerParameterSource [clazz=" + clazz + "]";
	}

}
