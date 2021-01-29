package io.github.swingboot.processor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public interface ProcessorDelegate {

	void process(TypeElement annotation, RoundEnvironment roundEnv);

	boolean canProcess(TypeElement annotation);
}
