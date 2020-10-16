package io.github.suice.command.annotation.installer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.EventParameterAndOrderAwareExecutor;
import io.github.suice.command.annotation.OnActionPerformed;

public class OnActionPerformedResolver extends AbstractListenerAnnotationResolver<OnActionPerformed, ActionListener> {

	private CommandExecutor executor;

	public OnActionPerformedResolver(CommandExecutor executor) {
		super(OnActionPerformed.class, "addActionListener", ActionListener.class);
		this.executor = executor;
	}

	@Override
	ActionListener createListener(OnActionPerformed annotation) {
		final Class<? extends Command<?>>[] commandTypes = annotation.value();

		final EventParameterAndOrderAwareExecutor eventParameterAndOrderAwareExecutor = new EventParameterAndOrderAwareExecutor(executor, commandTypes);

		final boolean anyModifier = annotation.modifiers() == OnActionPerformed.ANY_MODIFIER;
		return (ActionEvent event) -> {
			int eventModifiers = event.getModifiers();
			if (eventModifiers == annotation.modifiers() || anyModifier)
				eventParameterAndOrderAwareExecutor.execute(event);
		};
	}

}
