package io.github.swingboot.control.annotation.installer;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class AnnotationInstallerFactoryTests {

	@Test
	void main() {
		AnnotationInstaller installer = AnnotationInstallerFactory.get(OnActionPerformedInstaller.class);
		assertSame(installer, AnnotationInstallerFactory.get(OnActionPerformedInstaller.class));
		assertTrue(installer instanceof OnActionPerformedInstaller);
	}

}
