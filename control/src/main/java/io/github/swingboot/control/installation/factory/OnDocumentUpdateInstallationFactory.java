package io.github.swingboot.control.installation.factory;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class OnDocumentUpdateInstallationFactory implements InstallationFactory {

	@Override
	public Installation create(InstallationContext context) {
		final JTextComponent target = (JTextComponent) context.getTarget();

		DocumentListener listener = new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				consume(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				consume(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				consume(e);
			}

			private void consume(DocumentEvent e) {
				//Must be adapted to event object
				context.getEventConsumer().accept(new io.github.swingboot.control.event.DocumentEvent(e));
			}
		};

		return new Installation(() -> {
			target.getDocument().addDocumentListener(listener);
		}, () -> {
			target.getDocument().removeDocumentListener(listener);
		});
	}

}
