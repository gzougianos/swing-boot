package io.github.swingboot.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.processing.Processor;

import org.joor.CompileOptions;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.jupiter.api.Test;

class ForMethodInterceptionProcessorTests {
	/*
	 * Each compiled class should have its own name.
	 */

	@Test
	void onPrivateMethod() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public class N {"
				+ "	public N() {}"
				
				+ "	@AssertUi"
				+ "	private void doSomething() {}"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("N", classContent));
		assertTrue(ex.getMessage().toLowerCase().contains("private methods"));
	}

	@Test
	void onStaticMethod() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public class X {"
				+ "	public X() {}"
				
				+ "	@AssertUi"
				+ "	static void doSomething() {}"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("X", classContent));
		assertTrue(ex.getMessage().toLowerCase().contains("static methods"));
	}

	@Test
	void onFinalMethod() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public class S {"
				+ "	public S() {}"
				
				+ "	@AssertUi"
				+ "	final void doSomething() {}"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("S", classContent));
		assertTrue(ex.getMessage().toLowerCase().contains("final methods"));
	}

	@Test
	void onAbstractMethod() {
		String classContent =
		//@formatter:off
				"package test;"
	
				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public abstract class V {"
				+ "	public V() {}"
				
				+ "	@AssertUi"
				+ "	abstract void doSomething() {}"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("V", classContent));
		assertTrue(ex.getMessage().toLowerCase().contains("abstract methods"));
	}

	@Test
	void inFinalClass() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public final class I {"
				+ "	public I() {}"
				
				+ "	@AssertUi"
				+ "	void doSomething() {}"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("I", classContent));
		assertTrue(ex.getMessage().toLowerCase().contains("final class"));
	}

	@Test
	void inPrivateClass() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public class H {"
				+ "	public H() {}"
				
				+ " 	private class B{"
				+ "			@AssertUi"
				+ "			void doSomething() {}"
				+ "		}"
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("H", classContent));
		assertTrue(ex.getMessage().toLowerCase().contains("private class"));
	}

	@Test
	void inAbstractClass() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public abstract class G {"
				+ "	public G() {}"
				
				+ "	@AssertUi"
				+ "	void doSomething() {}"
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("G", classContent));
		assertTrue(ex.getMessage().toLowerCase().contains("abstract class"));
	}

	@Test
	void inInterface() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public interface J {"
				+ "	@AssertUi"
				+ "	void doSomething();"
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("J", classContent));
		assertTrue(ex.getMessage(), ex.getMessage().toLowerCase().contains("abstract method"));
	}

	@Test
	void inAnnotation() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public @interface K {"
				+ "	@AssertUi"
				+ "	String s();"
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("K", classContent));
		assertTrue(ex.getMessage(), ex.getMessage().toLowerCase().contains("abstract method"));
	}

	@Test
	void allGood() {
		String classContent =
		//@formatter:off
				"package test;"

				+ "import io.github.swingboot.concurrency.AssertUi;"
				
				+ "public class F {"
				+ "	public F() {}"
				
				+ "	@AssertUi"
				+ "	void doSomething() {}"
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("F", classContent));
	}

	void compile(String className, String classContent) {
		Processor processor = new BootProcessor();
		CompileOptions options = new CompileOptions().processors(processor);
		Reflect.compile("test." + className, classContent, options).type();
	}
}
