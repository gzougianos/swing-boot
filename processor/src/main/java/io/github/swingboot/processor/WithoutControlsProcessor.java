package io.github.swingboot.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("io.github.swingboot.control.annotation.WithoutControls")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class WithoutControlsProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement anno : annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(anno);
			annotatedElements.forEach(this::validate);
			System.out.println(annotatedElements);
		}
		return true;
	}

	private void validate(Element element) {
		final boolean isFinal = element.getModifiers().contains(Modifier.FINAL);
		final boolean isPrivate = element.getModifiers().contains(Modifier.PRIVATE);
		final boolean isAbstract = element.getModifiers().contains(Modifier.ABSTRACT);
		switch (element.getKind()) {
			case METHOD:
				if (isFinal) {
					error("WithoutControls cannot be used on final methods.", element);
				}

				if (isPrivate) {
					error("WithoutControls cannot be used on private methods.", element);
				}

				if (isAbstract) {
					error("WithoutControls cannot be used on abstract methods.", element);
				}

				boolean isClass = element.getEnclosingElement().getKind() == ElementKind.CLASS;
				if (isClass) {
					validateClassOfMethod(element.getEnclosingElement(), element);
				}
				break;
			default:
				//Should not happen
				error("WithoutControls can be used only in methods.", element);
		}
	}

	private void validateClassOfMethod(Element classElement, Element methodElement) {
		final boolean isFinal = classElement.getModifiers().contains(Modifier.FINAL);
		final boolean isPrivate = classElement.getModifiers().contains(Modifier.PRIVATE);
		final boolean isAbstract = classElement.getModifiers().contains(Modifier.ABSTRACT);
		if (isFinal) {
			error("WithoutControls cannot be used on methods of a final class.", methodElement);
		}
		if (isPrivate) {
			error("WithoutControls cannot be used on methods of a private class.", methodElement);
		}
		if (isAbstract) {
			error("WithoutControls cannot be used on methods of an abstract class.", methodElement);
		}

		boolean classIsInstallControlsAnnotation = isClassInstallControlsAnnotated(classElement);
		if (!classIsInstallControlsAnnotation) {
			error("WithoutControls annotated methods should be only within InstallControls annotated classes.",
					methodElement);
		}
	}

	private void error(String msg, Element element) {
		processingEnv.getMessager().printMessage(Kind.ERROR, msg, element);
	}

	private boolean isClassInstallControlsAnnotated(Element classElement) {
		for (AnnotationMirror annotationOnClass : classElement.getAnnotationMirrors()) {
			Name annotationName = annotationOnClass.getAnnotationType().asElement().getSimpleName();
			if ("InstallControls".contentEquals(annotationName)) {
				return true;
			}
		}
		return false;
	}

}
