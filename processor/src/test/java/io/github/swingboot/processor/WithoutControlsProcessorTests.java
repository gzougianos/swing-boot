package io.github.swingboot.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.processing.Processor;

import org.joor.CompileOptions;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.jupiter.api.Test;

class WithoutControlsProcessorTests {

	@Test
	void installControlsMissing() {
		String classContent =
		//@formatter:off
				"package withoutcontrols;"

				+ "import io.github.swingboot.control.annotation.*;"
				
				+ "public class N {"
				+ "	public N() {}"
				
				+ "	@WithoutControls"
				+ "	void doSomething() {}"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("N", classContent));
		assertTrue(ex.getMessage(), ex.getMessage().toLowerCase()
				.contains("can be used only in classes with InstallControls".toLowerCase()));
	}

	@Test
	void allOk() {
		String classContent =
		//@formatter:off
				"package withoutcontrols;"

				+ "import io.github.swingboot.control.annotation.*;"
				
				+ " @InstallControls"
				+ " public class G {"
				
				+ "	public G() {}"
				
				+ "	@WithoutControls"
				+ "	void doSomething() {}"
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("G", classContent));
	}

	void compile(String className, String classContent) {
		Processor processor = new BootProcessor();
		CompileOptions options = new CompileOptions().processors(processor);
		Reflect.compile("withoutcontrols." + className, classContent, options).type();
	}
}
