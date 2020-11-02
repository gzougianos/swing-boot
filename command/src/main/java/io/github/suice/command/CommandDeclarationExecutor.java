package io.github.suice.command;

import static io.github.suice.command.reflect.ReflectionUtils.equalsOrExtends;

import java.awt.AWTEvent;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.parameter.ParameterSource;

public class CommandDeclarationExecutor {
	private static final Logger log = LoggerFactory.getLogger(CommandDeclarationExecutor.class);
	private CommandExecutor executor;
	private ObjectOwnedCommandDeclaration cmdDeclaration;

	public CommandDeclarationExecutor(CommandExecutor executor, ObjectOwnedCommandDeclaration cmdDeclaration) {
		this.executor = executor;
		this.cmdDeclaration = cmdDeclaration;
	}

	public void execute(AWTEvent event) {
		Class<?> parType = cmdDeclaration.getCommandGenericParameterType();

		if (cmdDeclaration.getParameterSource().isPresent()) {
			executeInvokingSource(cmdDeclaration.getParameterSource().get(), event);
		} else if (equalsOrExtends(event.getClass(), parType)) {
			executeInjectingAwtEvent(event);
		} else {
			executor.execute(cmdDeclaration.getCommandType());
		}
	}

	private void executeInvokingSource(ParameterSource parameterSource, AWTEvent event) {
		Object parameterSourceValue = parameterSource.getValue(cmdDeclaration.getOwner(), event);
		Class<? extends Command<?>> commandType = cmdDeclaration.getCommandType();

		try {
			executor.getClass().getMethod("execute", Class.class, Object.class).invoke(executor, commandType,
					parameterSourceValue);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			log.error("Error executing command type " + commandType + " with parameter source " + parameterSource + ".", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void executeInjectingAwtEvent(AWTEvent event) {
		Class<? extends Command<AWTEvent>> parametrizedCmdType = (Class<? extends Command<AWTEvent>>) cmdDeclaration
				.getCommandType();
		executor.execute(parametrizedCmdType, event);
	}

}
