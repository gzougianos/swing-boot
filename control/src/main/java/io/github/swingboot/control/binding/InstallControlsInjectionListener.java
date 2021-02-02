package io.github.swingboot.control.binding;

import com.google.inject.spi.InjectionListener;

import io.github.swingboot.control.ControlInstaller;

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
