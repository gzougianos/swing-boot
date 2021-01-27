package io.github.swingboot.control.annotation.installation;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class ControlInstallationFactoriesTests {

	@Test
	void main() {
		ControlInstallationFactory factory = ControlInstallationFactories.get(OnActionPerformedInstallationFactory.class);
		assertSame(factory, ControlInstallationFactories.get(OnActionPerformedInstallationFactory.class));
		assertTrue(factory instanceof OnActionPerformedInstallationFactory);
	}

}
