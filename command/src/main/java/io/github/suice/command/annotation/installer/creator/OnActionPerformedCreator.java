package io.github.suice.command.annotation.installer.creator;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import io.github.suice.command.annotation.OnActionPerformed;

public class OnActionPerformedCreator implements ListenerCreator<OnActionPerformed, ActionListener> {

	@Override
	public ActionListener createListener(OnActionPerformed annotation, Consumer<AWTEvent> eventConsumer) {
		final boolean anyModifier = annotation.modifiers() == OnActionPerformed.ANY_MODIFIER;
		return (ActionEvent event) -> {
			int eventModifiers = event.getModifiers();
			if (eventModifiers == annotation.modifiers() || anyModifier)
				eventConsumer.accept(event);
		};
	}

}
