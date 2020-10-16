package io.github.suice.command.annotation.installer;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.ReflectionSupport;
import io.github.suice.command.annotation.OnActionPerformed;

public class OnActionPerformedResolver extends AbstractListenerAnnotationResolver<OnActionPerformed, ActionListener> {

	public OnActionPerformedResolver(CommandExecutor executor) {
		super(executor, OnActionPerformed.class, "addActionListener", ActionListener.class);
	}

	@Override
	ActionListener createListener(OnActionPerformed annotation) {
		final Class<? extends Command<?>>[] commandTypes = annotation.value();
		Map<Class<? extends Command<?>>, Class<? extends AWTEvent>> commandTypesWithParameterTypes = ReflectionSupport
				.defineParameterizedCommandTypes(commandTypes);

		final boolean anyModifier = annotation.modifiers() == OnActionPerformed.ANY_MODIFIER;
		return (ActionEvent event) -> {
			int eventModifiers = event.getModifiers();
			if (eventModifiers == annotation.modifiers() || anyModifier)
				executeConsideringEventParameterized(commandTypesWithParameterTypes, event);
		};
	}

}
