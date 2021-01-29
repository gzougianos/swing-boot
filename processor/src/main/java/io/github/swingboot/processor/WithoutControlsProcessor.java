package io.github.swingboot.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class WithoutControlsProcessor extends ForMethodInterceptionProcessor {
	private static final String ANNOTATION = "WithoutControls";

	public WithoutControlsProcessor(ProcessingEnvironment environment) {
		super(environment);
	}

	@Override
	protected void validateMethodElement(TypeElement annotation, Element methodElement) {
		super.validateMethodElement(annotation, methodElement);
		validateClassIsInstallControls(annotation, methodElement);
	}

	private void validateClassIsInstallControls(TypeElement annotation, Element methodElement) {
		final Element classElement = methodElement.getEnclosingElement();

		//@formatter:off
		boolean isInstallControls = classElement.getAnnotationMirrors().stream()
				.map(AnnotationMirror::getAnnotationType)
				.map(DeclaredType::asElement)
				.map(this::simpleNameOf)
				.anyMatch("InstallControls"::equals);
		//@formatter:on

		if (!isInstallControls)
			printError("WithoutControls can be used only in classes with InstallControls annotation.",
					methodElement, annotation);
	}

	@Override
	public boolean canProcess(TypeElement annotation) {
		return ANNOTATION.equals(simpleNameOf(annotation));
	}
}
