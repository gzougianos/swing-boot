package io.github.swingboot.control.installation.factory;

import java.util.HashMap;
import java.util.Map;

import io.github.swingboot.control.reflect.ReflectionException;

public final class ControlInstallationFactories {
	private static final Map<Class<? extends ControlInstallationFactory>, ControlInstallationFactory> instances = new HashMap<>();

	private ControlInstallationFactories() {
	}

	public static ControlInstallationFactory get(Class<? extends ControlInstallationFactory> factoryType) {
		if (instances.containsKey(factoryType))
			return instances.get(factoryType);

		try {
			ControlInstallationFactory newInstance = factoryType.newInstance();
			instances.put(factoryType, newInstance);
			return newInstance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ReflectionException(
					"Error creating control installation factory of type: " + factoryType, e);
		}
	}
}
