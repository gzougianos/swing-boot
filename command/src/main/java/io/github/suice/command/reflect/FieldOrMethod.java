package io.github.suice.command.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class FieldOrMethod {

	private AccessibleObject accessibleObject;

	public FieldOrMethod(AccessibleObject accessibleObject) {
		this.accessibleObject = accessibleObject;
		if (!isField() && !isMethod())
			throw new IllegalArgumentException("AccessibleObject " + accessibleObject + " is not field or method.");
	}

	public Class<?> getValueReturnType() {
		if (isField()) {
			return ((Field) accessibleObject).getType();
		}
		return ((Method) accessibleObject).getReturnType();
	}

	public boolean isField() {
		return accessibleObject instanceof Field;
	}

	public Class<?> getDeclaringClass() {
		if (isField())
			return ((Field) accessibleObject).getDeclaringClass();
		return ((Method) accessibleObject).getDeclaringClass();
	}

	public void ensureAccess() {
		accessibleObject.setAccessible(true);
	}

	public String getName() {
		if (isField())
			return ((Field) accessibleObject).getName();
		return ((Method) accessibleObject).getName();
	}

	public boolean isMethod() {
		return accessibleObject instanceof Method;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessibleObject);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldOrMethod other = (FieldOrMethod) obj;
		return Objects.equals(accessibleObject, other.accessibleObject);
	}

	public AccessibleObject getAccessibleObject() {
		return accessibleObject;
	}

	@Override
	public String toString() {
		return "FieldOrMethod [accessibleObject=" + accessibleObject + "]";
	}

}
