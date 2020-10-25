package io.github.suice.command.annotation.installer.creator;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import io.github.suice.command.EventParameterAwareExecutor;
import io.github.suice.command.annotation.OnComponentResized;

public class OnComponentResizedCreator implements ListenerCreator<OnComponentResized, ComponentListener> {

	@Override
	public ComponentListener createListener(OnComponentResized annotation, EventParameterAwareExecutor executor) {
		return new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				executor.execute(e);
			}
		};
	}

}
