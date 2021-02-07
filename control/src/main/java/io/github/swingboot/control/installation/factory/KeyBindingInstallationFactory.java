package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;

import io.github.swingboot.control.installation.annotation.KeyBinding;

public class KeyBindingInstallationFactory implements InstallationFactory {
	KeyBindingInstallationFactory() {
	}

	@Override
	public Installation create(InstallationContext context) {
		final KeyBinding binding = context.getAnnotationAs(KeyBinding.class);

		final Action action = new KeyBindingAction(context.getEventConsumer());
		final JComponent jComponent = getTargetJComponent(context.getTarget());

		final int condition = binding.when();
		final KeyStroke keyStroke = KeyStroke.getKeyStroke(binding.keyStroke());
		final String id = binding.id();

		return new Installation(() -> {
			jComponent.getInputMap(condition).put(keyStroke, id);
			jComponent.getActionMap().put(id, action);
		}, () -> {
			jComponent.getInputMap(condition).remove(keyStroke);
			jComponent.getActionMap().remove(id);
		});
	}

	private JComponent getTargetJComponent(Object target) {
		if (target instanceof JComponent)
			return (JComponent) target;

		if (target instanceof RootPaneContainer) {
			RootPaneContainer container = (RootPaneContainer) target;
			Component contentPane = container.getContentPane();
			if (contentPane instanceof JComponent)
				return (JComponent) contentPane;

			throw new UnsupportedOperationException("@KeyBinding cannot be installed to " + target.getClass()
					+ " when the ContentPane is not a JComponent.");
		}
		throw new UnsupportedOperationException(
				"@KeyBinding cannot be installed to target of type: " + target.getClass());
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
