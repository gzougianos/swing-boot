package io.github.suice.command;

import java.awt.AWTEvent;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;

public class EventParameterAndOrderAwareExecutor {
	private static final Logger log = LoggerFactory.getLogger(EventParameterAndOrderAwareExecutor.class);
	private CommandExecutor executor;
	private Class<? extends Command<?>>[] commandTypes;
	private Map<Class<? extends Command<?>>, Class<? extends AWTEvent>> commandTypesWithParameterTypes;

	public EventParameterAndOrderAwareExecutor(CommandExecutor executor, Class<? extends Command<?>>[] commandTypes) {
		this.executor = executor;
		this.commandTypes = commandTypes;

		commandTypesWithParameterTypes = new LinkedHashMap<>();
		analyzeCommandParameters();
	}

	private void analyzeCommandParameters() {
		for (Class<? extends Command<?>> cmdType : commandTypes) {
			commandTypesWithParameterTypes.put(cmdType, getParameterizedTypeOfOptional(cmdType));
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends AWTEvent> getParameterizedTypeOfOptional(Class<? extends Command<?>> commandType) {
		try {
			Parameter parameter = commandType.getMethod("execute", Optional.class).getParameters()[0];
			TypeLiteral<?> literal = TypeLiteral.get(parameter.getParameterizedType());
			Class<?> parType = literal.getFieldType(Optional.class.getDeclaredField("value")).getRawType();
			if (AWTEvent.class.isAssignableFrom(parType))
				return ((Class<? extends AWTEvent>) parType);
		} catch (NoSuchMethodException | NoSuchFieldException e) {
			log.error("Error checking if command type is event parametrized.", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void execute(AWTEvent event) {
		for (Class<? extends Command<?>> cmdType : commandTypesWithParameterTypes.keySet()) {
			Class<? extends AWTEvent> parType = commandTypesWithParameterTypes.get(cmdType);
			Class<? extends Command<AWTEvent>> parametrizedCmdType = (Class<? extends Command<AWTEvent>>) cmdType;
			if (parType != null && event.getClass().isAssignableFrom(parType))
				executor.execute(parametrizedCmdType, event);
			else
				executor.execute(cmdType);
		}
	}
}
