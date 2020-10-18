package io.github.suice.command.annotation.installer;

import java.lang.annotation.Annotation;

public interface AnnotationInstallerTestUtils {

	default Annotation annotationOfField(String fieldName) throws NoSuchFieldException, SecurityException {
		return getClass().getDeclaredField(fieldName).getDeclaredAnnotations()[0];
	}

}
