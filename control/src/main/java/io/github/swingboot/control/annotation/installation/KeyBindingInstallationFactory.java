package io.github.swingboot.control.annotation.installation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;

import io.github.swingboot.control.annotation.KeyBinding;

public class KeyBindingInstallationFactory implements ControlInstallationFactory {
	KeyBindingInstallationFactory() {
	}

	@Override
	public ControlInstallation createInstallation(Annotation annotation, Object target,
			Consumer<EventObject> eventConsumer) {
		final KeyBinding binding = (KeyBinding) annotation;
		final Action action = new KeyBindingAction(eventConsumer);
		final JComponent jComponent;

		if (target instanceof JComponent) {
			jComponent = (JComponent) target;
		} else if (target instanceof RootPaneContainer) {
			RootPaneContainer container = (RootPaneContainer) target;
			Component contentPane = container.getContentPane();
			if (contentPane instanceof JComponent) {
				jComponent = (JComponent) contentPane;
			} else {
				throw new UnsupportedOperationException("@KeyBinding cannot be installed to "
						+ target.getClass() + " when the ContentPane is not a JComponent.");
			}
		} else {
			throw new UnsupportedOperationException(
					"@KeyBinding cannot be installed to target of type: " + target.getClass());
		}

		final int condition = binding.when();
		final KeyStroke keyStroke = KeyStroke.getKeyStroke(binding.keyStroke());
		final String id = binding.id();

		return new ControlInstallation(() -> {
			jComponent.getInputMap(condition).put(keyStroke, id);
			jComponent.getActionMap().put(id, action);
		}, () -> {
			jComponent.getInputMap(condition).remove(keyStroke);
			jComponent.getActionMap().remove(id);
		});
	}

	private static class KeyBindingAction extends AbstractAction {
		private static final long serialVersionUID = -3628694774864195152L;
		private Consumer<EventObject> eventConsumer;

		public KeyBindingAction(Consumer<EventObject> eventConsumer) {
			this.eventConsumer = eventConsumer;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			eventConsumer.accept(e);
		}

	}

}
