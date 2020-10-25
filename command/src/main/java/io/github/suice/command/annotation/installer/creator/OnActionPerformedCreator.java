package io.github.suice.command.annotation.installer.creator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import io.github.suice.command.EventParameterAwareExecutor;
import io.github.suice.command.annotation.OnActionPerformed;

public class OnActionPerformedCreator implements ListenerCreator<OnActionPerformed, ActionListener> {

	@Override
	public ActionListener createListener(OnActionPerformed annotation, EventParameterAwareExecutor executor) {
		final boolean anyModifier = annotation.modifiers() == OnActionPerformed.ANY_MODIFIER;
		return (ActionEvent event) -> {
			int eventModifiers = event.getModifiers();
			if (eventModifiers == annotation.modifiers() || anyModifier)
				executor.execute(event);
		};
	}

}
