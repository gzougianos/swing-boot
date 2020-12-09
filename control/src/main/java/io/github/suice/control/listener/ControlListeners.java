package io.github.suice.control.listener;

import static java.util.Collections.unmodifiableList;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;

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

	public static <T extends EventListener> Collection<T> getAll(Class<T> listenerType, Component component) {
		T[] listeners = component.getListeners(listenerType);
		List<T> result = new ArrayList<>();
		for (T listener : listeners) {
			if (listener instanceof ControlListener) {
				result.add(listener);
			}
		}
		return unmodifiableList(result);
	}

}
