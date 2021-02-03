package io.github.swingboot.control.declaration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.reflect.ReflectionException;

class ControlTypeInfo {
	private static final String CONTROL_PERFORM_METHOD_NAME = "perform";
	private static final Map<Class<? extends Control<?>>, ControlTypeInfo> cache = new HashMap<>();
	private Class<? extends Control<?>> controlClass;
	private Class<?> parameterType;
	private boolean isParameterNullable;

	private ControlTypeInfo(Class<? extends Control<?>> controlClass) {
		this.controlClass = controlClass;
		analyzeParameter();
	}

	private void analyzeParameter() {
		for (TypeToken<?> typeToken : TypeToken.of(controlClass).getTypes()) {
			Class<?> rawType = typeToken.getRawType();
			if (rawType == Control.class) {
				try {
					TypeLiteral<?> typeLiteral = TypeLiteral.get(typeToken.getType());
					Method performMethod = rawType.getMethod(CONTROL_PERFORM_METHOD_NAME, Object.class);

					TypeLiteral<?> methodParameterTypeLiteral = typeLiteral.getParameterTypes(performMethod)
							.get(0);
					parameterType = methodParameterTypeLiteral.getRawType();

					checkNullableParameter();
				} catch (NoSuchMethodException | SecurityException e) {
					throw new ReflectionException(e);
				}
				return;
			}
		}

		throw new RuntimeException("Error analyzing control type: " + controlClass);
	}

	private void checkNullableParameter() {
		try {
			Method declaredMethod = controlClass.getMethod(CONTROL_PERFORM_METHOD_NAME, parameterType);
			for (Annotation ann : declaredMethod.getParameterAnnotations()[0]) {
				if (ann.annotationType().equals(Nullable.class)) {
					isParameterNullable = true;
					break;
				}
			}
		} catch (NoSuchMethodException e) {
			//Probaby an abstract class, count it as non nullable
		}
	}

	public Class<? extends Control<?>> getControlType() {
		return controlClass;
	}

	public boolean isParameterNullable() {
		return isParameterNullable;
	}

	public boolean isParameterless() {
		return parameterType == Void.class;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}

	static ControlTypeInfo of(Class<? extends Control<?>> controlClass) {
		return fromCacheOrNew(controlClass);
	}

	private static ControlTypeInfo fromCacheOrNew(Class<? extends Control<?>> controlClass) {
		if (cache.containsKey(controlClass))
			return cache.get(controlClass);

		ControlTypeInfo newTypeInfo = new ControlTypeInfo(controlClass);
		cache.put(controlClass, newTypeInfo);
		return newTypeInfo;
	}
}
