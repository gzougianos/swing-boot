package io.github.suice.command.annotation.installer.creator;

import java.awt.AWTEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.function.Consumer;

import io.github.suice.command.annotation.OnComponentResized;

public class OnComponentResizedCreator implements ListenerCreator<OnComponentResized, ComponentListener> {

	@Override
	public ComponentListener createListener(OnComponentResized annotation, Consumer<AWTEvent> eventConsumer) {
		return new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				eventConsumer.accept(e);
			}
		};
	}

}
