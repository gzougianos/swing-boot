package io.github.swingboot.control.installation.factory;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		assertTrue(installation.isInstalled());

		verify(install).run();
		verifyNoMoreInteractions(uninstall, install);

		installation.install();
		verifyNoMoreInteractions(uninstall, install);

		installation.uninstall();
		assertFalse(installation.isInstalled());
		verifyNoMoreInteractions(install);
		verify(uninstall).run();

		installation.uninstall();
		verifyNoMoreInteractions(uninstall, install);
	}

}
