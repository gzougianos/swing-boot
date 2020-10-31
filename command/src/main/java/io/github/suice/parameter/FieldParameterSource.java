package io.github.suice.parameter;

import java.awt.AWTEvent;
import java.lang.reflect.Field;
import java.util.Objects;

import io.github.suice.Untested;

@Untested
class FieldParameterSource implements ParameterSource {
	private final Field field;
	private final String id;

	FieldParameterSource(String id, Field field) {
		this.id = id;
		this.field = field;
	}

	@Override
	public Object getValue(Object sourceOwner, AWTEvent event) throws ParameterSourceException {
		ensureAccess();
		try {
			return field.get(sourceOwner);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ParameterSourceException("Error getting value from field paramater source: " + toString(), e);
		}
	}

	private void ensureAccess() {
		if (field.isAccessible())
			field.setAccessible(true);
	}

	@Override
	public Class<?> getValueReturnType() {
		return field.getType();
	}

	public Field getField() {
		return field;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "FieldParameterSource [id=" + id + ", field=" + field.getName() + ", class=" + field.getDeclaringClass() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldParameterSource other = (FieldParameterSource) obj;
		return Objects.equals(field, other.field) && Objects.equals(id, other.id);
	}

}
