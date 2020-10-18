package io.github.suice.command;

import java.awt.AWTEvent;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventParameterAndOrderAwareExecutor {
	private static final Map<Class<? extends Command<?>>, Class<? extends AWTEvent>> commandTypesWithParameterTypes = new LinkedHashMap<>();
	private CommandExecutor executor;
	private Class<? extends Command<?>>[] commandTypes;

	public EventParameterAndOrderAwareExecutor(CommandExecutor executor, Class<? extends Command<?>>[] commandTypes) {
		this.executor = executor;
		this.commandTypes = commandTypes;

		analyzeCommandTypeParameters();
	}

	private void analyzeCommandTypeParameters() {
		for (Class<? extends Command<?>> cmdType : commandTypes) {

			boolean alreadyAnalyzed = commandTypesWithParameterTypes.containsKey(cmdType);
			if (alreadyAnalyzed)
				continue;

			commandTypesWithParameterTypes.put(cmdType, getGenericAwtEventParameterType(cmdType));
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends AWTEvent> getGenericAwtEventParameterType(Class<? extends Command<?>> clazz) {
		for (Type interfaceType : clazz.getGenericInterfaces()) {
			boolean rawImplementation = interfaceType == clazz;
			if (rawImplementation)
				return null;

			if (interfaceType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) interfaceType;
				if (pt.getRawType() == Command.class) {
					Type type = pt.getActualTypeArguments()[0];
					if (typeIsClass(type)) {
						Class<?> typeAsClass = (Class<?>) type;
						if (equalsOrExtends(typeAsClass, AWTEvent.class))
							return (Class<? extends AWTEvent>) typeAsClass;
					}
					return null;
				}
			}
		}
		return null;
	}

	private static boolean typeIsClass(Type type) {
		return type instanceof Class<?>;
	}

	private static boolean equalsOrExtends(Class<?> typeA, Class<?> typeB) {
		return typeA.equals(typeB) || typeB.isAssignableFrom(typeA);
	}

	@SuppressWarnings("unchecked")
	public void execute(AWTEvent event) {
		for (Class<? extends Command<?>> cmdType : commandTypes) {
			Class<? extends AWTEvent> parType = commandTypesWithParameterTypes.get(cmdType);
			Class<? extends Command<AWTEvent>> parametrizedCmdType = (Class<? extends Command<AWTEvent>>) cmdType;
			if (parType != null && equalsOrExtends(event.getClass(), parType))
				executor.execute(parametrizedCmdType, event);
			else
				executor.execute(cmdType);
		}
	}
}
