package io.github.suice.command.annotation.installer;

import java.lang.annotation.Annotation;

abstract class AnnotationInstallerTestBase {

	Annotation annotationOfField(String fieldName) throws NoSuchFieldException, SecurityException {
		return getClass().getDeclaredField(fieldName).getDeclaredAnnotations()[0];
	}

}
