package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import io.github.swingboot.control.installation.annotation.OnMouseConstants;

abstract class OnMouseInstallationFactory implements InstallationFactory {

	@Override
	public final Installation create(InstallationContext context) {
		Component target = (Component) context.getTarget();
		final int modifiers = getAnnotationModifiers(context.getAnnotation());
		final int button = getAnnotationButton(context.getAnnotation());
		final int clickCount = getAnnotationClickCount(context.getAnnotation());

		final boolean anyButton = button == OnMouseConstants.ANY_BUTTON;
		final boolean anyModifier = modifiers == OnMouseConstants.ANY_MODIFIER;
		final boolean anyClickCount = clickCount == OnMouseConstants.ANY_CLICK_COUNT;

		Consumer<MouseEvent> mouseEventConsumer = event -> {
			boolean modifiersMatch = anyModifier || ((event.getModifiers() & modifiers) == modifiers);
			boolean buttonMatch = anyButton || button == event.getButton();
			boolean clickCountMatch = anyClickCount || clickCount == event.getClickCount();

			if (modifiersMatch && buttonMatch && clickCountMatch) {
				context.getEventConsumer().accept(event);
			}
		};

		MouseListener listener = createListener(mouseEventConsumer);

		return new Installation(() -> {
			target.addMouseListener(listener);
		}, () -> {
			target.removeMouseListener(listener);
		});
	}

	protected abstract int getAnnotationButton(Annotation onMouseAnnotation);

	protected abstract MouseListener createListener(Consumer<MouseEvent> eventConsumer);

	protected abstract int getAnnotationModifiers(Annotation onMouseAnnotation);

	protected abstract int getAnnotationClickCount(Annotation onMouseAnnotation);

}