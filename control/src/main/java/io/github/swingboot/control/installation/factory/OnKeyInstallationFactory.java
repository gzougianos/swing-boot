package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.swingboot.control.installation.annotation.OnKey;

abstract class OnKeyInstallationFactory implements InstallationFactory {

	@Override
	public final Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		int modifiers = getAnnotationModifiers(context.getAnnotation());
		int keyCode = getAnnotationKeyCode(context.getAnnotation());

		final boolean anyKeyCode = keyCode == OnKey.ANY_KEY_CODE;
		final boolean anyModifier = modifiers == OnKey.ANY_MODIFIER;

		Consumer<KeyEvent> keyEventConsumer = event -> {
			boolean modifiersMatch = anyModifier || ((event.getModifiers() & modifiers) == modifiers);
			boolean keyCodeMatch = anyKeyCode || event.getKeyCode() == keyCode;

			if (keyCodeMatch && modifiersMatch) {
				context.getEventConsumer().accept(event);
			}
		};

		KeyListener listener = createListener(keyEventConsumer);

		return new Installation(() -> {
			target.addKeyListener(listener);
		}, () -> {
			target.removeKeyListener(listener);
		});
	}

	protected abstract KeyListener createListener(Consumer<KeyEvent> eventConsumer);

	protected abstract int getAnnotationModifiers(Annotation onKeyAnnotation);

	protected abstract int getAnnotationKeyCode(Annotation onKeyAnnotation);

}