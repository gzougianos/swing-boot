package io.github.swingboot.control.annotation.installer;

public class ControlInstallation {

	private Runnable installation;
	private Runnable uninstallation;
	private boolean installed = false;

	ControlInstallation(Runnable installation, Runnable uninstallation) {
		this.installation = installation;
		this.uninstallation = uninstallation;
	}

	public void uninstall() {
		if (!installed)
			return;

		uninstallation.run();
		installed = false;
	}

	public void install() {
		if (installed)
			return;

		installation.run();
		installed = true;
	}

	public boolean isInstalled() {
		return installed;
	}
}
