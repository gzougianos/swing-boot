package io.github.suice.control;

import java.awt.Component;
import java.util.EventListener;

import io.github.suice.control.annotation.installer.ControlListener;

public final class ControlListeners {
	private ControlListeners() {
	}

	public static <T extends EventListener> T get(Class<T> listenerType, Component component) {
		T[] listeners = component.getListeners(listenerType);
		for (T listener : listeners) {
			if (listener instanceof ControlListener) {
				return listener;
			}
		}
		return null;
	}

}
