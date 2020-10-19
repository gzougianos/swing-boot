package io.github.suice.command.annotation.installer.creator;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.EventParameterAwareExecutor;
import io.github.suice.command.annotation.OnComponentResized;

public class OnComponentResizedCreator implements ListenerCreator<OnComponentResized, ComponentListener> {
	private CommandExecutor executor;

	public OnComponentResizedCreator(CommandExecutor executor) {
		this.executor = executor;
	}

	@Override
	public ComponentListener createListener(OnComponentResized annotation) {
		final Class<? extends Command<?>> commandType = annotation.value();

		final EventParameterAwareExecutor eventParameterAwareExecutor = new EventParameterAwareExecutor(executor, commandType);

		return new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				eventParameterAwareExecutor.execute(e);
			}
		};
	}

}