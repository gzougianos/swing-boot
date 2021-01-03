package io.github.suice.control.annotation.installer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;

import io.github.suice.control.annotation.KeyBinding;

public class KeyBindingInstaller implements AnnotationInstaller {

	@Override
	public void installAnnotation(Annotation annotation, Object target, Consumer<AWTEvent> eventConsumer) {
		final KeyBinding binding = (KeyBinding) annotation;
		final Action action = new KeyBindingAction(eventConsumer);

		if (target instanceof JComponent) {
			installToJComponent((JComponent) target, action, binding);
		} else if (target instanceof RootPaneContainer) {
			RootPaneContainer container = (RootPaneContainer) target;
			Component contentPane = container.getContentPane();
			if (contentPane instanceof JComponent) {
				installToJComponent((JComponent) contentPane, action, binding);
			} else {
				throw new UnsupportedOperationException("@KeyBinding cannot only be installed to "
						+ target.getClass() + " when the ContentPane is not a JComponent.");
			}
		} else {
			throw new UnsupportedOperationException(
					"@KeyBinding cannot be installed to target of type: " + target.getClass());
		}
	}

	private void installToJComponent(JComponent component, Action action, KeyBinding binding) {
		final int condition = binding.when();
		final KeyStroke keyStroke = KeyStroke.getKeyStroke(binding.keyStroke());
		final String id = binding.id();

		component.getInputMap(condition).put(keyStroke, id);
		component.getActionMap().put(id, action);
	}

	private static class KeyBindingAction extends AbstractAction {
		private static final long serialVersionUID = -3628694774864195152L;
		private Consumer<AWTEvent> eventConsumer;

		public KeyBindingAction(Consumer<AWTEvent> eventConsumer) {
			this.eventConsumer = eventConsumer;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			eventConsumer.accept(e);
		}

	}

}
