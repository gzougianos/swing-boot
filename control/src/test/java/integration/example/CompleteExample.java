package integration.example;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.github.suice.control.ControlModule;

public class CompleteExample {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Module viewModule = new AbstractModule() {
				@Override
				protected void configure() {
					requestInjection(this);
					bind(ClickCounterView.class);
				}
			};

			Injector injector = Guice.createInjector(viewModule, new ControlModule(IncreaseClickCounterControl.class));

			frame.add(injector.getInstance(ClickCounterView.class), BorderLayout.PAGE_START);
			frame.add(injector.getInstance(ClickCounterView.class), BorderLayout.PAGE_END);
			frame.pack();
			frame.setLocationByPlatform(true);
			frame.setVisible(true);
		});
	}
}
