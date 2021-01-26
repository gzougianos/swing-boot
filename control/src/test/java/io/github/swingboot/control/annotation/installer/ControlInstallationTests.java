package io.github.swingboot.control.annotation.installer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;

class ControlInstallationTests {

	@Test
	void main() {
		Runnable install = mock(Runnable.class);
		Runnable uninstall = mock(Runnable.class);
		ControlInstallation installation = new ControlInstallation(install, uninstall);
		installation.install();

		verify(install).run();
		verifyNoMoreInteractions(uninstall, install);

		installation.install();
		verifyNoMoreInteractions(uninstall, install);

		installation.uninstall();
		verifyNoMoreInteractions(install);
		verify(uninstall).run();

		installation.uninstall();
		verifyNoMoreInteractions(uninstall, install);
	}

}
