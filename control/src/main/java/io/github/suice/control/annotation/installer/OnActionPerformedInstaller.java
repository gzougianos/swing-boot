package io.github.suice.control.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.JTextField;

import io.github.suice.control.annotation.listener.OnActionPerformed;

public class OnActionPerformedInstaller implements AnnotationToComponentInstaller {

	@Override
	public boolean supportsAnnotation(Annotation annotation) {
		return annotation.annotationType().equals(OnActionPerformed.class);
	}

	@Override
	public void installAnnotation(Component component, Annotation annotation, Consumer<AWTEvent> eventConsumer) {
		OnActionPerformed onActionPerformed = (OnActionPerformed) annotation;
		ActionListener listener = createListener(eventConsumer, onActionPerformed);

		if (component instanceof AbstractButton) {
			((AbstractButton) component).addActionListener(listener);
		} else if (component instanceof JTextField) {
			((JTextField) component).addActionListener(listener);
		} else {
			throw new IllegalArgumentException(
					"Action listener cannot be installed to component of type: " + component.getClass());
		}
	}

	private ActionListener createListener(Consumer<AWTEvent> eventConsumer, OnActionPerformed onActionPerformed) {
		final boolean anyModifier = onActionPerformed.modifiers() == OnActionPerformed.ANY_MODIFIER;
		return event -> {
			int eventModifiers = event.getModifiers();
			if (eventModifiers == onActionPerformed.modifiers() || anyModifier)
				eventConsumer.accept(event);
		};
	}

}
