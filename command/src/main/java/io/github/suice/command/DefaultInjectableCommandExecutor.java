package io.github.suice.command;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

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
		commandInstance.execute(empty());
	}

	@Override
	public <S, T extends Command<S>> void execute(Class<T> commandClass, S parameters) {
		Command<S> commandInstance = injector.getInstance(commandClass);
		commandInstance.execute(ofNullable(parameters));
	}

}
