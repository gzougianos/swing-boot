package io.github.swingboot.control.installation.factory;

import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

public class OnCaretUpdateInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		JTextComponent target = (JTextComponent) context.getTarget();

		CaretListener listener = context.getEventConsumer()::accept;

		return new Installation(() -> {
			target.addCaretListener(listener);
		}, () -> {
			target.removeCaretListener(listener);
		});
	}

}
