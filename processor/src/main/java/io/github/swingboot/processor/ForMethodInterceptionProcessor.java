package io.github.swingboot.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

public class ForMethodInterceptionProcessor implements ProcessorDelegate {
	//@formatter:off
	private static final List<String> SUPPORTED_ANNOTATIONS = Arrays.asList(
			"InUi", "InBackground", 
			"AssertBackground", "AssertUi");
	
	private final Messager messager;
	//@formatter:on
	public ForMethodInterceptionProcessor(Messager messager) {
		this.messager = messager;
	}

	@Override
	public void process(TypeElement annotation, RoundEnvironment roundEnv) {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
		annotatedElements.forEach(methodElement -> {
			validateMethodElement(annotation, methodElement);
		});
	}

	protected void validateMethodElement(TypeElement annotation, Element methodElement) {
		validateAbsentMethodModifier(annotation, methodElement, Modifier.STATIC);
		validateAbsentMethodModifier(annotation, methodElement, Modifier.PRIVATE);
		validateAbsentMethodModifier(annotation, methodElement, Modifier.ABSTRACT);
		validateAbsentMethodModifier(annotation, methodElement, Modifier.FINAL);
		validateEnclosingElementIsClass(annotation, methodElement);
		validateAbsentClassModifier(annotation, methodElement, Modifier.PRIVATE);
		validateAbsentClassModifier(annotation, methodElement, Modifier.FINAL);
		validateAbsentClassModifier(annotation, methodElement, Modifier.ABSTRACT);
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

	protected void validateEnclosingElementIsClass(TypeElement annotation, Element element) {
		boolean isClass = element.getEnclosingElement().getKind() == ElementKind.CLASS;
		if (!isClass) {
			String error = "%s can be used only on methods of a class.";
			error = String.format(error, simpleNameOf(annotation));
			printError(error, element, annotation);
		}
	}

	protected void validateAbsentMethodModifier(TypeElement annotation, Element methodElement,
			Modifier modifier) {
		final boolean containsModifier = methodElement.getModifiers().contains(modifier);
		if (containsModifier) {
			String error = "%s cannot be used on %s methods.";
			error = String.format(error, simpleNameOf(annotation), modifier.toString().toLowerCase());
			printError(error, methodElement, annotation);
		}
	}

	protected String simpleNameOf(TypeElement element) {
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

	@Override
	public boolean canProcess(TypeElement annotation) {
		return SUPPORTED_ANNOTATIONS.contains(simpleNameOf(annotation));
	}

}
