package io.github.swingboot.processor;

import java.util.EventObject;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class ParameterSourceProcessor extends AbstractProcessorDelegate {

	public ParameterSourceProcessor(ProcessingEnvironment environment) {
		super(environment);
	}

	@Override
	public void process(TypeElement annotation, RoundEnvironment roundEnv) {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
		annotatedElements.forEach(element -> validateMethodElement(annotation, element, annotatedElements));
	}

	private void validateMethodElement(TypeElement annotationElement, Element methodOrFieldElement,
			Set<? extends Element> annotatedElements) {

		validateNotEmptyNotThisSourceId(annotationElement, methodOrFieldElement);
		validateUniqueParameterSourceId(annotationElement, methodOrFieldElement, annotatedElements);
		if (methodOrFieldElement instanceof ExecutableElement) {
			validateAbsentMethodModifier(annotationElement, methodOrFieldElement, Modifier.ABSTRACT);
			validateReturnTypeNotVoid(annotationElement, (ExecutableElement) methodOrFieldElement);
			validateZeroOrOneAwtEvent(annotationElement, (ExecutableElement) methodOrFieldElement);
		}
	}

	private void validateNotEmptyNotThisSourceId(TypeElement annotationElement,
			Element methodOrFieldElement) {
		AnnotationValue sourceIdValue = getAnnotationPropertyValue("value", annotationElement,
				methodOrFieldElement);

		String sourceId = (String) sourceIdValue.getValue();
		if (sourceId.isEmpty()) {
			printError("Parameter sources cannot have empty id", methodOrFieldElement, annotationElement,
					sourceIdValue);
			return;
		}
		if ("this".equals(sourceId)) {
			printError("Parameter sources cannot have 'this' id", methodOrFieldElement, annotationElement,
					sourceIdValue);
			return;
		}
	}

	private void validateZeroOrOneAwtEvent(TypeElement annotationElement, ExecutableElement methodElement) {
		List<? extends VariableElement> parameters = methodElement.getParameters();
		if (parameters.isEmpty())
			return;

		if (parameters.size() > 1) {
			printError("A ParameterSource can have zero or only one EventObject parameter.", methodElement,
					annotationElement);
		}

		VariableElement parameter = parameters.get(0);
		TypeMirror eventObjMirror = environment.getElementUtils()
				.getTypeElement(EventObject.class.getCanonicalName()).asType();

		Types typeUtils = environment.getTypeUtils();
		boolean extendsEventObject = typeUtils.isAssignable(parameter.asType(), eventObjMirror);
		boolean equalsEventObject = typeUtils.isSameType(eventObjMirror, parameter.asType());

		if (!extendsEventObject && !equalsEventObject) {
			printError("A ParameterSource can have zero or only one EventObject parameter.", methodElement,
					annotationElement);
		}
	}

	private void validateReturnTypeNotVoid(TypeElement annotationElement, ExecutableElement methodElement) {
		if (isVoid(methodElement.getReturnType())) {
			printError("ParameterSource methods cannot be void.", methodElement, annotationElement);
		}
	}

	protected void validateUniqueParameterSourceId(TypeElement annotationElement, Element methodElement,
			Set<? extends Element> annotatedElements) {

		String parameterSourceId = (String) getAnnotationPropertyValue("value", annotationElement,
				methodElement).getValue();

		for (Element otherElement : annotatedElements) {
			if (otherElement == methodElement) {
				continue;
			}

			String parameterSourceId2 = (String) getAnnotationPropertyValue("value", annotationElement,
					otherElement).getValue();
			if (parameterSourceId.equals(parameterSourceId2)) {
				String error = "Parameter Source with id %s already exists in class.";
				error = String.format(error, parameterSourceId);
				printError(error, otherElement, annotationElement);
			}
		}
	}

	@Override
	public boolean canProcess(TypeElement annotation) {
		return "ParameterSource".equals(simpleNameOf(annotation));
	}

}
