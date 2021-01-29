package io.github.swingboot.processor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

//@formatter:off
@SupportedAnnotationTypes({ 
	"io.github.swingboot.control.annotation.*", 
	"io.github.swingboot.concurrency.*" 
	})
//@formatter:on
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BootProcessor extends AbstractProcessor {
	private Set<ProcessorDelegate> delegates = new HashSet<>();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		delegates.add(new ForMethodInterceptionProcessor(processingEnv.getMessager()));
		delegates.add(new WithoutControlsProcessor(processingEnv.getMessager()));
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement anno : annotations) {
			//@formatter:off
			delegates.stream()
					.filter(delegate -> delegate.canProcess(anno))
					.forEach(delegate -> delegate.process(anno, roundEnv));
			//@formatter:on
		}

		return true;
	}
}
