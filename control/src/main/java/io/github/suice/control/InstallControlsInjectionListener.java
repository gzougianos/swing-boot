package io.github.suice.control;

import com.google.inject.spi.InjectionListener;

class InstallControlsInjectionListener implements InjectionListener<Object> {
	private ControlInstaller controlInstaller;

	InstallControlsInjectionListener() {
	}

	@Override
	public void afterInjection(Object injectee) {
		boolean notInitiatedYet = controlInstaller == null;
		if (notInitiatedYet)
			return;

		controlInstaller.installControls(injectee);
	}

	public void setControlInstaller(ControlInstaller controlInstaller) {
		this.controlInstaller = controlInstaller;
	}

}
