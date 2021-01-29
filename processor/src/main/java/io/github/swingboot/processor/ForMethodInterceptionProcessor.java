package io.github.swingboot.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class ForMethodInterceptionProcessor extends AbstractProcessorDelegate {
	//@formatter:off
	private static final List<String> SUPPORTED_ANNOTATIONS = Arrays.asList(
			"InUi", "InBackground", 
			"AssertBackground", "AssertUi");
	
	//@formatter:on
	public ForMethodInterceptionProcessor(ProcessingEnvironment environment) {
		super(environment);
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

	protected void validateEnclosingElementIsClass(TypeElement annotation, Element element) {
		boolean isClass = element.getEnclosingElement().getKind() == ElementKind.CLASS;
		if (!isClass) {
			String error = "%s can be used only on methods of a class.";
			error = String.format(error, simpleNameOf(annotation));
			printError(error, element, annotation);
		}
	}

	@Override
	public boolean canProcess(TypeElement annotation) {
		return SUPPORTED_ANNOTATIONS.contains(simpleNameOf(annotation));
	}

}
