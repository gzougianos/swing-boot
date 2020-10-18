package io.github.suice.command;

import javax.inject.Inject;

import com.google.inject.Injector;

public class DefaultInjectableCommandExecutor implements CommandExecutor {
	private Injector injector;

	@Inject
	public DefaultInjectableCommandExecutor(Injector injector) {
		this.injector = injector;
	}

	@Override
	public <T extends Command<?>> void execute(Class<T> commandClass) {
		Command<?> commandInstance = injector.getInstance(commandClass);
		commandInstance.execute(null);
	}

	@Override
	public <S, T extends Command<S>> void execute(Class<T> commandClass, S parameter) {
		Command<S> commandInstance = injector.getInstance(commandClass);
		commandInstance.execute(parameter);
	}

}
