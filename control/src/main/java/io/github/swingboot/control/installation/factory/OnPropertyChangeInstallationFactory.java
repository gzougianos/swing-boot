package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.beans.PropertyChangeListener;

import io.github.swingboot.control.installation.annotation.OnPropertyChange;

public class OnPropertyChangeInstallationFactory implements InstallationFactory {
	OnPropertyChangeInstallationFactory() {
	}

	@Override
	public Installation create(InstallationContext context) {
		final Component component = (Component) context.getTarget();
		OnPropertyChange annotation = context.getAnnotationAs(OnPropertyChange.class);

		final String property = annotation.property();
		final boolean anyProperty = property.equals(OnPropertyChange.ANY_PROPERTY);

		final PropertyChangeListener listener = event -> {
			context.getEventConsumer().accept(event);
		};

		if (anyProperty) {
			return new Installation(() -> {
				component.addPropertyChangeListener(listener);
			}, () -> {
				component.removePropertyChangeListener(listener);
			});
		} else {
			return new Installation(() -> {
				component.addPropertyChangeListener(property, listener);
			}, () -> {
				component.removePropertyChangeListener(property, listener);
			});
		}
	}
}
