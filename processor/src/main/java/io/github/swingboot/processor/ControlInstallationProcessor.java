package io.github.swingboot.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class ControlInstallationProcessor extends AbstractProcessorDelegate {

	public ControlInstallationProcessor(ProcessingEnvironment environment) {
		super(environment);
	}

	@Override
	public void process(TypeElement annotation, RoundEnvironment roundEnv) {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
		annotatedElements.forEach(element -> validate(annotation, element, roundEnv));
	}

	private void validate(TypeElement annotation, Element element, RoundEnvironment roundEnv) {
		validateCorrectTargetType(annotation, element, roundEnv);
	}

	private void validateCorrectTargetType(TypeElement annotation, Element element,
			RoundEnvironment roundEnv) {
		for (AnnotationMirror mirror : annotation.getAnnotationMirrors()) {
			if ("DeclaresControlInstallation".equals(simpleNameOf(mirror))) {
				List<TypeMirror> targetTypes = getTargetTypesFromDeclaresControlInstallation(mirror);
				validateFieldTypeIsTargetType(annotation, element, targetTypes);

				validateParameterSource(annotation, element);
			}
		}
	}

	private void validateParameterSource(TypeElement annotation, Element element) {

		AnnotationValue parameterSourceValue = getAnnotationPropertyValue("parameterSource", annotation,
				element);

		validateParameterSourceExistsInClass(annotation, element, parameterSourceValue);
		validateParameterSourceGivenOnlyWhenControlIsParameterized(annotation, element, parameterSourceValue);
	}

	private void validateParameterSourceGivenOnlyWhenControlIsParameterized(TypeElement annotation,
			Element element, AnnotationValue parameterSourceValue) {
		final String parameterSourceId = (String) parameterSourceValue.getValue();
		TypeMirror controlTypeMirror = getDeclaredControlType(annotation, element);
		TypeMirror controlGenericParameterType = getControlGenericParameterType(annotation, element);

		boolean isParameterized = !isVoid(controlGenericParameterType);
		boolean parameterSourceGiven = !parameterSourceId.isEmpty();

		if (parameterSourceGiven && !isParameterized) {
			String error = "Parameter source given but %s takes no parameters";
			error = String.format(error, simpleNameOf(controlTypeMirror));
			printError(error, element, annotation, parameterSourceValue);
		} else if (!parameterSourceGiven && isParameterized) {
			boolean isNullableParameterized = isNullableParameterized(controlTypeMirror);
			if (isNullableParameterized)
				return;

			String error = "Parameter source not given for not @javax.annotation.Nullable parameter of %s";
			error = String.format(error, simpleNameOf(controlTypeMirror));
			printError(error, element, annotation, getAnnotationPropertyValue("value", annotation, element));
		}

	}

	private boolean isNullableParameterized(TypeMirror controlTypeMirror) {
		TypeElement asElement = (TypeElement) environment.getTypeUtils().asElement(controlTypeMirror);
		TypeElement superControlTypeElement = getSuperControlTypeElement();
		ExecutableElement performMethodElement = (ExecutableElement) superControlTypeElement
				.getEnclosedElements().get(0);

		//@formatter:off
		List<? extends ExecutableElement> methodElementsOfImplementation = environment.getTypeUtils()
				.asElement(controlTypeMirror).getEnclosedElements().stream()
				.filter(ExecutableElement.class::isInstance)
				.map(ExecutableElement.class::cast)
				.collect(Collectors.toList());
		//@formatter:on

		for (ExecutableElement methodElement : methodElementsOfImplementation) {
			boolean overrides = environment.getElementUtils().overrides(methodElement, performMethodElement,
					asElement);
			if (overrides) {
				VariableElement parameterElement = methodElement.getParameters().get(0);
				return parameterElement.getAnnotationMirrors().stream().anyMatch(this::isNullableAnnotation);
			}
		}

		return false;
	}

	private boolean isNullableAnnotation(AnnotationMirror mirror) {
		TypeMirror nullable = environment.getElementUtils().getTypeElement("javax.annotation.Nullable")
				.asType();
		return environment.getTypeUtils().isSameType(mirror.getAnnotationType(), nullable);
	}

	private TypeMirror getDeclaredControlType(TypeElement annotation, Element element) {
		AnnotationValue controlValue = getAnnotationPropertyValue("value", annotation, element);

		DeclaredType declaredType = (DeclaredType) controlValue.getValue();
		return declaredType.asElement().asType();
	}

	private TypeMirror getControlGenericParameterType(TypeElement annotation, Element element) {
		TypeMirror controlTypeMirror = getDeclaredControlType(annotation, element);
		List<? extends TypeMirror> directSupertypes = environment.getTypeUtils()
				.directSupertypes(controlTypeMirror);

		TypeElement controlSuperMirror = getSuperControlTypeElement();

		for (TypeMirror superMirror : directSupertypes) {
			Element asElement = environment.getTypeUtils().asElement(superMirror);
			if (asElement.equals(controlSuperMirror)) {
				DeclaredType asDeclaredType = (DeclaredType) superMirror;
				if (!asDeclaredType.getTypeArguments().isEmpty())
					return asDeclaredType.getTypeArguments().get(0);
			}
		}

		return environment.getElementUtils().getTypeElement("java.lang.Object").asType();
	}

	private TypeElement getSuperControlTypeElement() {
		TypeElement controlSuperMirror = environment.getElementUtils()
				.getTypeElement("io.github.swingboot.control.Control");
		return controlSuperMirror;
	}

	private void validateParameterSourceExistsInClass(TypeElement annotation, Element element,
			AnnotationValue parameterSourceValue) {

		final String parameterSourceId = (String) parameterSourceValue.getValue();

		if (parameterSourceId.isEmpty() || "this".equals(parameterSourceId))
			return;

		Element classElement = element.getKind() == ElementKind.CLASS ? element
				: element.getEnclosingElement();

		List<? extends Element> allMembers = environment.getElementUtils()
				.getAllMembers((TypeElement) classElement);

		for (Element nestedElement : allMembers) {
			AnnotationMirror parameterSourceMirror = getParameterSourceMirror(
					nestedElement.getAnnotationMirrors());

			if (parameterSourceMirror == null)
				continue;

			String otherParameterSource = (String) getAnnotationPropertyValue("value", parameterSourceMirror)
					.getValue();

			boolean found = otherParameterSource.equals(parameterSourceId);
			if (found)
				return;

		}
		String error = "Parameter source with id %s does not exist in class";
		error = String.format(error, parameterSourceId);
		printError(error, element, annotation, parameterSourceValue);
	}

	protected boolean isParameterSourceMirror(AnnotationMirror mirror) {
		return simpleNameOf(mirror).equals("ParameterSource");
	}

	protected AnnotationMirror getParameterSourceMirror(List<? extends AnnotationMirror> list) {
		for (AnnotationMirror mirror : list) {
			if (simpleNameOf(mirror).equals("ParameterSource")) {
				return mirror;
			}
		}
		return null;
	}

	protected void validateFieldTypeIsTargetType(TypeElement annotation, Element element,
			List<TypeMirror> targetTypes) {
		TypeMirror fieldType = element.asType();

		final Types typeUtils = environment.getTypeUtils();

		boolean extendsATarget = targetTypes.stream()
				.anyMatch(target -> typeUtils.isAssignable(fieldType, target));

		if (extendsATarget)
			return;

		boolean equalsATarget = targetTypes.stream()
				.anyMatch(target -> typeUtils.isSameType(fieldType, target));

		if (equalsATarget)
			return;

		//@formatter:off
		String availableTargets = targetTypes.stream()
				.map(environment.getTypeUtils()::asElement)
				.map(this::simpleNameOf)
				.collect(Collectors.joining(", "));
		//@formatter:on

		String error = "%s can be used only to %s";
		error = String.format(error, simpleNameOf(annotation), availableTargets);
		printError(error, element, annotation);

	}

	private List<TypeMirror> getTargetTypesFromDeclaresControlInstallation(AnnotationMirror mirror) {
		List<TypeMirror> result = new ArrayList<>();
		AnnotationValue annotationPropertyValue = getAnnotationPropertyValue("targetTypes", mirror);

		@SuppressWarnings("unchecked")
		List<AnnotationValue> targetTypesList = (List<AnnotationValue>) annotationPropertyValue.getValue();
		for (AnnotationValue targetType : targetTypesList) {
			DeclaredType declaredType = (DeclaredType) targetType.getValue();
			TypeMirror mirrora = declaredType.asElement().asType();
			result.add(mirrora);
		}
		return result;
	}

	@Override
	public boolean canProcess(TypeElement annotation) {
		//@formatter:off
		return annotation.getAnnotationMirrors().stream()
				.map(this::simpleNameOf)
				.anyMatch(n -> "DeclaresControlInstallation".equals(n));
		//@formatter:on
	}

}
