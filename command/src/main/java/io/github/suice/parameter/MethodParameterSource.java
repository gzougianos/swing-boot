package io.github.suice.parameter;

import static io.github.suice.command.reflect.ReflectionUtils.equalsOrExtends;

import java.awt.AWTEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

import io.github.suice.Untested;

@Untested
class MethodParameterSource implements ParameterSource {
	private final String id;
	private final Method method;

	MethodParameterSource(String id, Method method) throws ParameterSourceException {
		this.id = id;
		this.method = method;

		checkIdNotThis();
		checkIdNotEmpty();
		checkIfItsVoid();
		checkZeroOrOneAwtEventParameter();
	}

	private void checkIfItsVoid() {
		if (method.getReturnType().equals(Void.TYPE)) {
			throw new ParameterSourceException("ParameterSource method " + method.getName() + " in class"
					+ method.getDeclaringClass().getSimpleName() + " is void. Parameter sources cannot be void.");
		}
	}

	private void checkZeroOrOneAwtEventParameter() {
		if (hasNoParameters())
			return;

		if (method.getParameterCount() == 1) {
			Parameter parameter = method.getParameters()[0];
			if (equalsOrExtends(parameter.getType(), AWTEvent.class)) {
				return;
			}
		}
		throw new ParameterSourceException("ParameterSource method " + method.getName() + " in class"
				+ method.getDeclaringClass().getSimpleName() + " can have zero or only one AWTEvent parameter.");
	}

	private void checkIdNotEmpty() {
		if (id.isEmpty()) {
			throw new ParameterSourceException("@ParameterSource cannot have empty string as id. Found in method `"
					+ method.getName() + "` of " + method.getDeclaringClass() + ".");
		}
	}

	private void checkIdNotThis() {
		if ("this".equals(id)) {
			throw new ParameterSourceException("@ParameterSource cannot have `this` as id. Found in method `" + method.getName()
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
	public Object getValue(Object sourceOwner, AWTEvent event) throws ParameterSourceException {
		ensureAccess();
		try {
			if (hasNoParameters())
				return method.invoke(sourceOwner);

			if (eventTypeMatchesParameterType(event))
				return method.invoke(sourceOwner, event);

			return method.invoke(sourceOwner, (AWTEvent) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ParameterSourceException("Error invoking method " + method.getName() + " of " + method.getDeclaringClass()
					+ " with event parameter " + event.getClass() + ".", e);
		}
	}

	private boolean eventTypeMatchesParameterType(AWTEvent event) {
		Class<?> parameterType = method.getParameters()[0].getType();
		return equalsOrExtends(event.getClass(), parameterType);
	}

	private boolean hasNoParameters() {
		return method.getParameterCount() == 0;
	}

	private void ensureAccess() {
		if (method.isAccessible())
			method.setAccessible(true);
	}

	@Override
	public String toString() {
		return "MethodParameterSource [id=" + id + ", method=" + method.getName() + ", class=" + method.getDeclaringClass() + "]";
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
