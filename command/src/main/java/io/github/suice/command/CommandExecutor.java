package io.github.suice.command;

public interface CommandExecutor {
	<T extends Command<?>> void execute(Class<T> commandClass);

	<S, T extends Command<S>> void execute(Class<T> commandClass, S parameters);

	//	<S extends AWTEvent, T extends Command<? extends S>> void execute(Class<T> commandClass, S parameters);
}
