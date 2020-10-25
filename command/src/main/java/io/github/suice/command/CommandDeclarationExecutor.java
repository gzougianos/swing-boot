package io.github.suice.command;

import static io.github.suice.command.reflect.ReflectionUtils.equalsOrExtends;

import java.awt.AWTEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.command.reflect.FieldOrMethod;

public class CommandDeclarationExecutor implements EventParameterAwareExecutor {
	private static final Logger log = LoggerFactory.getLogger(CommandDeclarationExecutor.class);
	private CommandExecutor executor;
	private ObjectOwnedCommandDeclaration cmdDeclaration;

	public CommandDeclarationExecutor(CommandExecutor executor, ObjectOwnedCommandDeclaration cmdDeclaration) {
		this.executor = executor;
		this.cmdDeclaration = cmdDeclaration;
	}

	@Override
	public void execute(AWTEvent event) {
		Class<?> parType = cmdDeclaration.getCommandGenericParameterType();

		if (cmdDeclaration.getParameterSource().isPresent())
			executeInvokingSource(cmdDeclaration.getParameterSource().get(), event);
		else if (equalsOrExtends(event.getClass(), parType) && equalsOrExtends(parType, AWTEvent.class)) {
			executeInjectingAwtEvent(event);
		} else {
			executor.execute(cmdDeclaration.getCommandType());
		}
	}

	private void executeInvokingSource(FieldOrMethod fieldOrMethod, AWTEvent event) {
		Object parameterSourceValue = getValueParameterSourceValue(fieldOrMethod, event);

		Class<? extends Command<?>> commandType = cmdDeclaration.getCommandType();
		try {
			executor.getClass().getMethod("execute", Class.class, Object.class).invoke(executor, commandType,
					parameterSourceValue);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			log.error("Error executing command type " + commandType + " with parameter source " + fieldOrMethod + ".", e);
		}
	}

	private Object getValueParameterSourceValue(FieldOrMethod fieldOrMethod, AWTEvent event) {
		try {
			fieldOrMethod.ensureAccess();
			if (fieldOrMethod.isField()) {
				Field field = (Field) fieldOrMethod.getAccessibleObject();
				return field.get(cmdDeclaration.getOwner());
			} else {
				Method method = (Method) fieldOrMethod.getAccessibleObject();
				if (method.getParameterCount() == 1) {
					return executeParameterSourceMethodWithAwtEventParameter(event, method);
				}
				return method.invoke(cmdDeclaration.getOwner());
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			log.error("Error gettingValue from parameter source:" + fieldOrMethod, e);
		}
		return null;
	}

	private Object executeParameterSourceMethodWithAwtEventParameter(AWTEvent event, Method method)
			throws IllegalAccessException, InvocationTargetException {

		Class<?> argType = method.getParameters()[0].getType();
		if (equalsOrExtends(event.getClass(), argType))
			return method.invoke(cmdDeclaration.getOwner(), event);

		return method.invoke(cmdDeclaration.getOwner(), (Object) null);
	}

	@SuppressWarnings("unchecked")
	private void executeInjectingAwtEvent(AWTEvent event) {
		Class<? extends Command<AWTEvent>> parametrizedCmdType = (Class<? extends Command<AWTEvent>>) cmdDeclaration
				.getCommandType();
		executor.execute(parametrizedCmdType, event);
	}

}
