package io.github.suice.command;

import com.google.inject.spi.InjectionListener;

public class CommandInitializableInjectionListener implements InjectionListener<Object> {
	private CommandInitializer commandInitializer;

	public CommandInitializableInjectionListener() {
	}

	@Override
	public void afterInjection(Object injectee) {
		boolean notInitiatedYet = commandInitializer == null;
		if (notInitiatedYet)
			return;

		commandInitializer.initializeCommands(injectee);
	}

	public void setCommandInitializer(CommandInitializer commandInitializer) {
		this.commandInitializer = commandInitializer;
	}

}
