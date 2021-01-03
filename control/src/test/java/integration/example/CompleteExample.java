package integration.example;

import javax.swing.SwingUtilities;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.github.suice.control.ControlModule;

public class CompleteExample {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Module viewModule = new AbstractModule() {
				@Override
				protected void configure() {
					requestInjection(this);
					bind(ClickCounterView.class);
					bind(MainView.class);
				}
			};

			Injector injector = Guice.createInjector(viewModule,
					new ControlModule(IncreaseClickCounterControl.class));

			MainView mainView = injector.getInstance(MainView.class);
			mainView.setVisible(true);
		});
	}
}
