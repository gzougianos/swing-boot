package io.github.swingboot.example.editor;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.swingboot.concurrency.ConcurrencyModule;
import io.github.swingboot.control.binding.ControlModule;
import io.github.swingboot.example.editor.control.SaveDocumentControl;
import io.github.swingboot.example.editor.model.ModelModule;
import io.github.swingboot.example.editor.view.MainView;
import io.github.swingboot.example.editor.view.ViewModule;

public class Launcher {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			setLaf();
			//@formatter:off
			Injector injector = Guice.createInjector(
					new ViewModule(), 
					new ModelModule(),
					new ControlModule(SaveDocumentControl.class),
					new ConcurrencyModule());
			//@formatter:on

			injector.getInstance(MainView.class).setVisible(true);
		});
	}

	private static void setLaf() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
