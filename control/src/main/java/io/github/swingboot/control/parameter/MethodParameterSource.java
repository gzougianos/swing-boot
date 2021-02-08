package io.github.swingboot.control.parameter;

import static io.github.swingboot.control.ParameterSource.THIS;
import static io.github.swingboot.control.reflect.ReflectionUtils.equalsOrExtends;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.EventObject;
import java.util.Objects;

import io.github.swingboot.control.reflect.ReflectionException;

class MethodParameterSource implements ParameterSource {
	private final String id;
	private final Method method;

	MethodParameterSource(String id, Method method) throws InvalidParameterSourceException {
		this.id = id;
		this.method = method;

		checkIdNotThis();
		checkIdNotEmpty();
		checkNotVoid();
		checkZeroOrOneEventObjectParameter();
	}

	private void checkNotVoid() {
		if (method.getReturnType().equals(Void.TYPE)) {
			throw new InvalidParameterSourceException("ParameterSource method " + method.getName()
					+ " in class " + method.getDeclaringClass().getSimpleName()
					+ " is void. Method parameter sources cannot be void.");
		}
	}

	private void checkZeroOrOneEventObjectParameter() {
		if (hasNoParameters())
			return;

		if (method.getParameterCount() == 1) {
			Parameter parameter = method.getParameters()[0];
			if (equalsOrExtends(parameter.getType(), EventObject.class)) {
				return;
			}
		}
		throw new InvalidParameterSourceException("ParameterSource method " + method.getName() + " in class"
				+ method.getDeclaringClass().getSimpleName()
				+ " can have zero or only one java.util.EventObject parameter.");
	}

	private void checkIdNotEmpty() {
		if (id == null || id.isEmpty()) {
			throw new InvalidParameterSourceException(
					"@ParameterSource cannot have empty string as id. Found in method `" + method.getName()
							+ "` of " + method.getDeclaringClass() + ".");
		}
	}

	private void checkIdNotThis() {
		if (THIS.equals(id)) {
			throw new InvalidParameterSourceException(
					"@ParameterSource cannot have `this` as id. Found in method `" + method.getName()
							+ "` of " + method.getDeclaringClass() + ".");
		}
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Class<?> getValueReturnType() {
		return method.getReturnType();
	}

	@Override
	public Object getValue(Object sourceOwner, EventObject event) {
		ensureAccess();
		try {
			if (hasNoParameters())
				return method.invoke(sourceOwner);

			if (event != null && eventTypeMatchesParameterType(event))
				return method.invoke(sourceOwner, event);

			return method.invoke(sourceOwner, (EventObject) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ReflectionException("Error invoking method " + method.getName() + " of "
					+ method.getDeclaringClass() + " with event parameter " + event + ".", e);
		}
	}

	private boolean eventTypeMatchesParameterType(EventObject event) {
		Class<?> parameterType = method.getParameters()[0].getType();
		return equalsOrExtends(event.getClass(), parameterType);
	}

	private boolean hasNoParameters() {
		return method.getParameterCount() == 0;
	}

	private void ensureAccess() {
		if (!method.isAccessible())
			method.setAccessible(true);
	}

	@Override
	public String toString() {
		return "MethodParameterSource [id=" + id + ", method=" + method.getName() + ", class="
				+ method.getDeclaringClass() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, method);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodParameterSource other = (MethodParameterSource) obj;
		return Objects.equals(id, other.id) && Objects.equals(method, other.method);
	}

}
