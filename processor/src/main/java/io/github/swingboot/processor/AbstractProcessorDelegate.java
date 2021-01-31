package io.github.swingboot.processor;

import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

public abstract class AbstractProcessorDelegate implements ProcessorDelegate {
	protected Messager messager;
	protected ProcessingEnvironment environment;

	public AbstractProcessorDelegate(ProcessingEnvironment environment) {
		this.environment = environment;
		this.messager = environment.getMessager();
	}

	@Override
	public abstract void process(TypeElement annotation, RoundEnvironment roundEnv);

	@Override
	public abstract boolean canProcess(TypeElement annotation);

	protected void validateAbsentMethodModifier(TypeElement annotation, Element methodElement,
			Modifier modifier) {
		final boolean containsModifier = methodElement.getModifiers().contains(modifier);
		if (containsModifier) {
			String error = "%s cannot be used on %s methods.";
			error = String.format(error, simpleNameOf(annotation), modifier.toString().toLowerCase());
			printError(error, methodElement, annotation);
		}
	}

	protected void validateAbsentClassModifier(TypeElement annotation, Element methodElement,
			Modifier modifier) {
		final Element classElement = methodElement.getEnclosingElement();
		final boolean containsModifier = classElement.getModifiers().contains(modifier);
		if (containsModifier) {
			String error = "%s cannot be used on methods of a %s class.";
			error = String.format(error, simpleNameOf(annotation), modifier.toString().toLowerCase());
			printError(error, methodElement, annotation);
		}
	}

	protected String simpleNameOf(Element element) {
		return element.getSimpleName().toString();
	}

	protected String simpleNameOf(TypeMirror mirror) {
		return simpleNameOf(environment.getTypeUtils().asElement(mirror));
	}

	protected String simpleNameOf(AnnotationMirror mirror) {
		return simpleNameOf(mirror.getAnnotationType().asElement());
	}

	protected void printError(String msg, Element element, TypeElement annotation) {
		for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
			if (annotation.equals(mirror.getAnnotationType().asElement())) {
				messager.printMessage(Kind.ERROR, msg, element, mirror);
				return;
			}
		}
		messager.printMessage(Kind.ERROR, msg, element);
	}

	protected void printError(String msg, Element element, TypeElement annotation, AnnotationValue value) {
		for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
			if (annotation.equals(mirror.getAnnotationType().asElement())) {
				messager.printMessage(Kind.ERROR, msg, element, mirror, value);
				return;
			}
		}
		messager.printMessage(Kind.ERROR, msg, element);
	}

	protected boolean isVoid(TypeMirror mirror) {
		String mirrorAsString = String.valueOf(mirror);
		return "void".equals(mirrorAsString) || "java.lang.Void".equals(mirrorAsString);
	}

	protected Map<? extends ExecutableElement, ? extends AnnotationValue> getAnnotationValues(
			TypeElement annotationElement, Element element) {

		AnnotationMirror asMirror = asMirror(annotationElement, element);
		return environment.getElementUtils().getElementValuesWithDefaults(asMirror);
	}

	protected AnnotationValue getAnnotationPropertyValue(String property, TypeElement annotationElement,
			Element element) {

		Map<? extends ExecutableElement, ? extends AnnotationValue> values = getAnnotationValues(
				annotationElement, element);

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
			if (property.contentEquals(e.getKey().getSimpleName()))
				return e.getValue();
		}

		throw new RuntimeException("Annotation " + annotationElement + " on element " + element
				+ " does not contain a '" + property + "' property.");
	}

	protected AnnotationValue getAnnotationPropertyValue(String property, AnnotationMirror mirror) {

		Map<? extends ExecutableElement, ? extends AnnotationValue> values = environment.getElementUtils()
				.getElementValuesWithDefaults(mirror);

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
			if (property.contentEquals(e.getKey().getSimpleName()))
				return e.getValue();
		}

		throw new RuntimeException(
				"Annotation " + mirror + " does not contain a '" + property + "' property.");
	}

	protected AnnotationMirror asMirror(TypeElement annotation, Element element) {
		for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
			if (mirror.getAnnotationType().equals(annotation.asType())) {
				return mirror;
			}
		}
		throw new RuntimeException("Element " + element + " is not annotated with " + annotation);
	}
}
