package io.github.swingboot.control.installation.factory;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import io.github.swingboot.control.installation.annotation.OnItemStateChanged;

public class OnItemStateChangedInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		ItemSelectable target = (ItemSelectable) context.getTarget();
		OnItemStateChanged onItemStateChanged = context.getAnnotationAs(OnItemStateChanged.class);

		ItemListener listener = event -> {
			boolean selectedState = event.getStateChange() == ItemEvent.SELECTED;
			if (onItemStateChanged.selected().matches(selectedState)) {
				context.getEventConsumer().accept(event);
			}
		};

		return new Installation(() -> {
			target.addItemListener(listener);
		}, () -> {
			target.removeItemListener(listener);
		});
	}

}
