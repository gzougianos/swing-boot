package io.github.suice.control.annotation.installer;

import java.util.HashMap;
import java.util.Map;

import io.github.suice.control.reflect.ReflectionException;

public final class AnnotationInstallerFactory {
	private static final Map<Class<? extends AnnotationInstaller>, AnnotationInstaller> instances = new HashMap<>();

	private AnnotationInstallerFactory() {
	}

	public static AnnotationInstaller get(Class<? extends AnnotationInstaller> installerType) {
		if (instances.containsKey(installerType))
			return instances.get(installerType);

		try {
			AnnotationInstaller newInstance = installerType.newInstance();
			instances.put(installerType, newInstance);
			return newInstance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ReflectionException("Error creating annotation installer of type: " + installerType, e);
		}
	}
}
