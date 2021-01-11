package io.github.swingboot.control;

import java.awt.Component;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public final class PassiveComponents {
	private static final Map<Component, Void> components = new WeakHashMap<>();

	private PassiveComponents() {
	}

	public static final void put(Component... comps) {
		for (Component c : comps) {
			components.put(c, null);
		}
	}

	public static final void put(Collection<Component> comps) {
		for (Component c : comps) {
			components.put(c, null);
		}
	}

	public static final void remove(Component... comps) {
		for (Component c : comps) {
			components.remove(c, null);
		}
	}

	public static final void remove(Collection<Component> comps) {
		for (Component c : comps) {
			components.remove(c, null);
		}
	}

	public static final void whilePassive(Component component, Runnable r) {
		put(component);
		r.run();
		remove(component);
	}

	public static final <T extends Component> void whilePassive(T component, Consumer<T> consumer) {
		put(component);
		consumer.accept(component);
		remove(component);
	}

	public static final void whilePassive(Collection<Component> components, Runnable r) {
		put(components);
		r.run();
		remove(components);
	}

	static boolean contains(Component c) {
		return components.containsKey(c);
	}
}
