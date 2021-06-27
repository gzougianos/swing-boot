package io.github.swingboot.control.installation.factory;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import io.github.swingboot.control.installation.annotation.OnActionPerformed;

public class OnActionPerformedInstallationFactory implements InstallationFactory {
	OnActionPerformedInstallationFactory() {
	}

	@Override
	public Installation create(InstallationContext context) {
		final OnActionPerformed onActionPerformed = context.getAnnotationAs(OnActionPerformed.class);
		final int declaredModifiers = onActionPerformed.modifiers();
		final boolean anyModifier = declaredModifiers == OnActionPerformed.ANY_MODIFIER;

		ActionListener listener = e -> {
			int eventModifiers = e.getModifiers();
			boolean modifiersMatch = (eventModifiers & declaredModifiers) == declaredModifiers;

			if (anyModifier || modifiersMatch)
				context.getEventConsumer().accept(e);
		};

		final Object target = context.getTarget();

		return createInstallation(listener, target);
	}

	private Installation createInstallation(ActionListener listener, final Object target) {
		if (target instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) target;
			return new Installation(() -> button.addActionListener(listener),
					() -> button.removeActionListener(listener));
		}

		if (target instanceof JComboBox<?>) {
			JComboBox<?> comboBox = (JComboBox<?>) target;
			return new Installation(() -> comboBox.addActionListener(listener),
					() -> comboBox.removeActionListener(listener));
		}

		if (target instanceof JTextField) {
			JTextField field = (JTextField) target;
			return new Installation(() -> field.addActionListener(listener),
					() -> field.removeActionListener(listener));
		}

		//Should never happen
		throw new UnsupportedOperationException(
				"@OnActionPerformed cannot be installed to target of type: " + target.getClass());
	}

}
