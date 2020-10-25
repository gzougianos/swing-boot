package io.github.suice.command;

import java.awt.Component;
import java.lang.annotation.Annotation;

public class AnnotationInstallation {

	private Annotation annotation;
	private Component component;
	private ParameterSourceMember parameterSourceMember;

	public AnnotationInstallation(Annotation annotation, Component component, ParameterSourceMember parameterSourceMember) {
		this.annotation = annotation;
		this.component = component;
		this.parameterSourceMember = parameterSourceMember;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public Component getComponent() {
		return component;
	}

	public ParameterSourceMember getParameterSourceMember() {
		return parameterSourceMember;
	}

	@Override
	public String toString() {
		return "AnnotationInstallation [annotation=" + annotation + ", component=" + component + ", parameterSourceMember="
				+ parameterSourceMember + "]";
	}

}
