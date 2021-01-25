package example;

import java.awt.Color;

import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.ControlModule;

public class MoveMainViewRightControl implements Control<Void> {
	private MainView mainView;

	@Inject
	public MoveMainViewRightControl(MainView mainView) {
		this.mainView = mainView;
	}

	@Override
	public void perform(Void parameter) {
		mainView.setLocation(mainView.getLocation().x + 20, mainView.getLocation().y);
		mainView.doWithControlsUninstalled();

		for (int i = 0; i < 2000; i++) {
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
			mainView.setBackground(Color.black);
			mainView.dispose();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("destroyed: CONTROL");
	}
}
