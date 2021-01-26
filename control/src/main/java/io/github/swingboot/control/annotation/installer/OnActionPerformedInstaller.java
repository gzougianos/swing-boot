package io.github.swingboot.control.annotation.installer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import io.github.swingboot.control.annotation.OnActionPerformed;

public class OnActionPerformedInstaller implements AnnotationInstaller {

	@Override
	public ControlInstallation createInstallation(Annotation annotation, Object target,
			Consumer<EventObject> eventConsumer) {
		OnActionPerformed onActionPerformed = (OnActionPerformed) annotation;
		final boolean anyModifier = onActionPerformed.modifiers() == OnActionPerformed.ANY_MODIFIER;

		Predicate<ActionEvent> eventPredicate = event -> {
			int eventModifiers = event.getModifiers();
			return anyModifier || eventModifiers == onActionPerformed.modifiers();
		};
		ControlInstallation installation;
		Listener listener = new Listener(eventPredicate, eventConsumer);

		if (target instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) target;

			installation = new ControlInstallation(() -> button.addActionListener(listener),
					() -> button.removeActionListener(listener));

		} else if (target instanceof JComboBox<?>) {
			JComboBox<?> comboBox = (JComboBox<?>) target;

			installation = new ControlInstallation(() -> comboBox.addActionListener(listener),
					() -> comboBox.removeActionListener(listener));

		} else if (target instanceof JTextField) {
			JTextField field = (JTextField) target;

			installation = new ControlInstallation(() -> field.addActionListener(listener),
					() -> field.removeActionListener(listener));
		} else {
			throw new UnsupportedOperationException(
					"@OnActionPerformed cannot be installed to target of type: " + target.getClass());
		}
		return installation;
	}

	private static class Listener implements ActionListener {
		private Consumer<EventObject> eventConsumer;
		private Predicate<ActionEvent> controlFirePredicate;

		public Listener(Predicate<ActionEvent> eventPredicate, Consumer<EventObject> eventConsumer) {
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
