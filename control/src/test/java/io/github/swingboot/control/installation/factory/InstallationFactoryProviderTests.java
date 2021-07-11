package io.github.swingboot.control.installation.factory;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class InstallationFactoryProviderTests {

	@Test
	void main() {
		InstallationFactory factory = InstallationFactoryProvider
				.get(OnActionPerformedInstallationFactory.class);
		assertSame(factory, InstallationFactoryProvider.get(OnActionPerformedInstallationFactory.class));
		assertTrue(factory instanceof OnActionPerformedInstallationFactory);
	}

}
