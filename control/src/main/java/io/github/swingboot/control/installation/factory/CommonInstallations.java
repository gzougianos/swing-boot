package io.github.swingboot.control.installation.factory;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;

class CommonInstallations {
	private CommonInstallations() {
	}

	static Installation mouse(Component c, MouseListener listener) {
		return new Installation(() -> {
			c.addMouseListener(listener);
		}, () -> {
			c.removeMouseListener(listener);
		});
	}

	static Installation component(Component c, ComponentListener listener) {
		return new Installation(() -> {
			c.addComponentListener(listener);
		}, () -> {
			c.removeComponentListener(listener);
		});
	}

	static Installation mouseMotion(Component c, MouseMotionListener listener) {
		return new Installation(() -> {
			c.addMouseMotionListener(listener);
		}, () -> {
			c.removeMouseMotionListener(listener);
		});
	}

	static Installation focus(Component c, FocusListener listener) {
		return new Installation(() -> {
			c.addFocusListener(listener);
		}, () -> {
			c.removeFocusListener(listener);
		});
	}

	static Installation window(Window w, WindowListener listener) {
		return new Installation(() -> {
			w.addWindowListener(listener);
		}, () -> {
			w.removeWindowListener(listener);
		});
	}
}
