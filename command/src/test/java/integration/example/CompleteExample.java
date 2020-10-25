package integration.example;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.ProvisionListener;

import io.github.suice.command.CommandModule;

public class CompleteExample {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Module viewModule = new AbstractModule() {
				@Override
				protected void configure() {
					bindListener(Matchers.any(), new ProvisionListener() {

						@Override
						public <T> void onProvision(ProvisionInvocation<T> provision) {
						}
					});
					requestInjection(this);
					bind(ClickCounterView.class);
					bind(AdvancedClickCounterView.class);
				}

				@Inject
				private void e() {
					System.out.println("EE");
				}
			};

			Injector injector = Guice.createInjector(viewModule, new CommandModule(IncreaseClickCounterCommand.class));

			frame.add(injector.getInstance(ClickCounterView.class), BorderLayout.PAGE_START);
			frame.add(injector.getInstance(CompositeView.class), BorderLayout.CENTER);
			frame.add(injector.getInstance(AdvancedClickCounterView.class), BorderLayout.PAGE_END);
			frame.pack();
			frame.setLocationByPlatform(true);
			frame.setVisible(true);
		});
	}
}
