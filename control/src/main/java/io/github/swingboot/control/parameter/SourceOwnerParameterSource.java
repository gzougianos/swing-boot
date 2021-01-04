package io.github.swingboot.control.parameter;

import static io.github.swingboot.control.annotation.ParameterSource.THIS;

import java.awt.AWTEvent;

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
	public Object getValue(Object sourceOwner, AWTEvent event) {
		return sourceOwner;
	}

	@Override
	public String toString() {
		return "SourceOwnerParameterSource [clazz=" + clazz + "]";
	}

}
