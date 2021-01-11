package io.github.swingboot.control.parameter;

import static io.github.swingboot.control.annotation.ParameterSource.THIS;

import java.lang.reflect.Field;
import java.util.EventObject;
import java.util.Objects;

import io.github.swingboot.control.reflect.ReflectionException;

class FieldParameterSource implements ParameterSource {
	private final Field field;
	private final String id;

	FieldParameterSource(String id, Field field) throws InvalidParameterSourceException {
		this.id = id;
		this.field = field;

		checkIdNotEmpty();
		checkIdNotThis();
	}

	private void checkIdNotEmpty() {
		if (id == null || id.isEmpty()) {
			throw new InvalidParameterSourceException(
					"@ParameterSource cannot have empty string as id. Found in field `" + field.getName()
							+ "` of " + field.getDeclaringClass() + ".");
		}
	}

	private void checkIdNotThis() {
		if (THIS.equals(id)) {
			throw new InvalidParameterSourceException(
					"@ParameterSource cannot have `this` as id. Found in field `" + field.getName() + "` of "
							+ field.getDeclaringClass() + ".");
		}
	}

	@Override
	public Object getValue(Object sourceOwner, EventObject event) {
		ensureAccess();
		try {
			return field.get(sourceOwner);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ReflectionException("Error getting value from field paramater source: " + toString(),
					e);
		}
	}

	private void ensureAccess() {
		if (!field.isAccessible())
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
		return "FieldParameterSource [id=" + id + ", field=" + field.getName() + ", class="
				+ field.getDeclaringClass() + "]";
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
