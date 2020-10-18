package io.github.suice.command.annotation.installer.creator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.EventParameterAwareExecutor;
import io.github.suice.command.annotation.OnActionPerformed;

public class OnActionPerformedCreator implements ListenerCreator<OnActionPerformed, ActionListener> {

	private CommandExecutor executor;

	public OnActionPerformedCreator(CommandExecutor executor) {
		this.executor = executor;
	}

	@Override
	public ActionListener createListener(OnActionPerformed annotation) {
		final Class<? extends Command<?>> commandType = annotation.value();

		final EventParameterAwareExecutor eventParameterAwareExecutor = new EventParameterAwareExecutor(executor, commandType);

		final boolean anyModifier = annotation.modifiers() == OnActionPerformed.ANY_MODIFIER;
		return (ActionEvent event) -> {
			int eventModifiers = event.getModifiers();
			if (eventModifiers == annotation.modifiers() || anyModifier)
				eventParameterAwareExecutor.execute(event);
		};
	}

}
