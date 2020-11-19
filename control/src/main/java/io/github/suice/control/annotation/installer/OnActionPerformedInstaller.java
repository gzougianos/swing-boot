package io.github.suice.control.annotation.installer;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.AbstractButton;
import javax.swing.JTextField;

import io.github.suice.control.annotation.OnActionPerformed;
import io.github.suice.control.listener.ControlListener;

public class OnActionPerformedInstaller implements AnnotationInstaller {

	@Override
	public void installAnnotation(Annotation annotation, Object target, Consumer<AWTEvent> eventConsumer) {
		OnActionPerformed onActionPerformed = (OnActionPerformed) annotation;
		final boolean anyModifier = onActionPerformed.modifiers() == OnActionPerformed.ANY_MODIFIER;

		Predicate<ActionEvent> eventPredicate = event -> {
			int eventModifiers = event.getModifiers();
			return anyModifier || eventModifiers == onActionPerformed.modifiers();
		};

		if (target instanceof AbstractButton) {
			((AbstractButton) target).addActionListener(new Listener(eventPredicate, eventConsumer));
		} else if (target instanceof JTextField) {
			((JTextField) target).addActionListener(new Listener(eventPredicate, eventConsumer));
		} else {
			throw new IllegalArgumentException("Action listener cannot be installed to component of type: " + target.getClass());
		}
	}

	private static class Listener implements ActionListener, ControlListener {
		private Consumer<AWTEvent> eventConsumer;
		private Predicate<ActionEvent> controlFirePredicate;

		public Listener(Predicate<ActionEvent> eventPredicate, Consumer<AWTEvent> eventConsumer) {
			this.controlFirePredicate = eventPredicate;
			this.eventConsumer = eventConsumer;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if (controlFirePredicate.test(event))
				eventConsumer.accept(event);
		}

	}

}
