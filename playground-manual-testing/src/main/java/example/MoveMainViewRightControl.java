package example;

import javax.inject.Inject;

import io.github.swingboot.control.Control;

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

	}

}
