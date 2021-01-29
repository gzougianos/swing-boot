package io.github.swingboot.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
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

	protected void printError(String msg, Element element, TypeElement annotation) {
		for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
			if (annotation.equals(mirror.getAnnotationType().asElement())) {
				messager.printMessage(Kind.ERROR, msg, element, mirror);
				return;
			}
		}
		messager.printMessage(Kind.ERROR, msg, element);
	}
}
