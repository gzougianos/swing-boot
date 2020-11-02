package io.github.suice.command;

import com.google.inject.spi.InjectionListener;

public class CommandInstallerInjectionListener implements InjectionListener<Object> {
	private CommandInstaller commandInstaller;

	public CommandInstallerInjectionListener() {
	}

	@Override
	public void afterInjection(Object injectee) {
		boolean notInitiatedYet = commandInstaller == null;
		if (notInitiatedYet)
			return;

		commandInstaller.installCommands(injectee);
	}

	public void setCommandInstaller(CommandInstaller commandInstaller) {
		this.commandInstaller = commandInstaller;
	}

}
