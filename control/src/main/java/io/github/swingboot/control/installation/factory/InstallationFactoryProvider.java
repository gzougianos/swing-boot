package io.github.swingboot.control.installation.factory;

import java.util.HashMap;
import java.util.Map;

import io.github.swingboot.control.reflect.ReflectionException;

public final class InstallationFactoryProvider {
	private static final Map<Class<? extends InstallationFactory>, InstallationFactory> instances = new HashMap<>();

	private InstallationFactoryProvider() {
	}

	public static InstallationFactory get(Class<? extends InstallationFactory> factoryType) {
		if (instances.containsKey(factoryType))
			return instances.get(factoryType);

		try {
			InstallationFactory newInstance = factoryType.newInstance();
			instances.put(factoryType, newInstance);
			return newInstance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ReflectionException(
					"Error creating control installation factory of type: " + factoryType, e);
		}
	}
}
