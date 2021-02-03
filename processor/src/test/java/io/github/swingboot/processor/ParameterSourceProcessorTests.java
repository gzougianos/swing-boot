package io.github.swingboot.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.processing.Processor;

import org.joor.CompileOptions;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.jupiter.api.Test;

class ParameterSourceProcessorTests {

	@Test
	void sameId() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class G {"
				+ "	public G() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething() {"
				+ " 	return 5;"
				+ " }"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething2() {"
				+ " 	return 5;"
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("G", classContent));
		assertExceptionWithMessage(exception, "already exists");
	}

	@Test
	void emptyId() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class ER {"
				+ "	public ER() {}"
				
				+ "	@ParameterSource(\"\")"
				+ "	private int doSomething2() {"
				+ " 	return 5;"
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("ER", classContent));
		assertExceptionWithMessage(exception, "empty id");
	}

	@Test
	void thisId() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class ER {"
				+ "	public ER() {}"
				
				+ "	@ParameterSource(\"this\")"
				+ "	private int doSomething2() {"
				+ " 	return 5;"
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("ER", classContent));
		assertExceptionWithMessage(exception, "'this' id");
	}

	@Test
	void sameIdInParent() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class GB {"
				+ "	public GB() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething() {"
				+ " 	return 5;"
				+ " }"
				
				+ " static class Child extends GB{ "
				
				+ "		@ParameterSource(\"someid\")"
				+ "		private int doSomethingElse() {"
				+ " 		return 5;"
				+ " 	}"
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("GB", classContent));
		assertExceptionWithMessage(exception, "already exists");
	}

	@Test
	void sameIdOnMethodAndField() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class GA {"
				
				+ "	@ParameterSource(\"someid\")"
				+ " private int x=5;"
				
				+ "	public GA() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething() {"
				+ " 	return 5;"
				+ " }"
				
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("GA", classContent));
		assertExceptionWithMessage(exception, "already exists");
	}

	@Test
	void sameIdOnFields() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class GR {"
				
				+ "	@ParameterSource(\"someid\")"
				+ " private int x=5;"
				
				+ "	@ParameterSource(\"someid\")"
				+ " private int y=5;"
				
				+ "	public GR() {}"	
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("GR", classContent));
		assertExceptionWithMessage(exception, "already exists");
	}

	@Test
	void abstractMethod() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public abstract class F {"
				+ "	public F() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething() {"
				+ " 	return 5;"
				+ " }"
				
				+ "	@ParameterSource(\"someid2\")"
				+ "	abstract int doSomething2();"
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("F", classContent));
		assertExceptionWithMessage(exception, "abstract methods");
	}

	@Test
	void voidMethod() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class C {"
				+ "	public C() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething() {"
				+ " 	return 5;"
				+ " }"
				
				+ "	@ParameterSource(\"someid2\")"
				+ "	private Void doSomething2() { }"
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("C", classContent));
		assertExceptionWithMessage(exception, "methods cannot be void");
	}

	@Test
	void moreThanOneArgument() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class DE {"
				+ "	public DE() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething(int k,int s) {"
				+ " 	return 5;"
				+ " }"
				
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("DE", classContent));
		assertExceptionWithMessage(exception,
				"A ParameterSource can have zero or only one EventObject parameter.");
	}

	@Test
	void oneNonEventObjectParameter() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class DS {"
				+ "	public DS() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething(int k) {"
				+ " 	return 5;"
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException exception = assertThrows(ReflectException.class, () -> compile("DS", classContent));
		assertExceptionWithMessage(exception,
				"A ParameterSource can have zero or only one EventObject parameter.");
	}

	@Test
	void allOkNoParameters() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				
				+ "public class N {"
				+ "	public N() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething() {"
				+ " 	return 5;"
				+ " }"
				
				+ "	@ParameterSource(\"someid2\")"
				+ "	private int doSomething2() {"
				+ " 	return 5;"
				+ " }"
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("N", classContent));
	}

	@Test
	void allOkOneEventParameter() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				+ "import java.util.EventObject;"
				
				+ "public class NE {"
				+ "	public NE() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething(EventObject obj) {"
				+ " 	return 5;"
				+ " }"
				
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("NE", classContent));
	}

	@Test
	void allOkOneExtendedEventParameter() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				+ "import java.awt.event.*;"
				
				+ "public class NA {"
				+ "	public NA() {}"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething(ComponentEvent obj) {"
				+ " 	return 5;"
				+ " }"
				
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("NA", classContent));
	}

	@Test
	void allOkOneMethodAndOneField() {
		String classContent =
		//@formatter:off
				"package parametersource;"

				+ "import io.github.swingboot.control.*;"
				+ "import io.github.swingboot.control.installation.annotation.*;"
				+ "import java.awt.event.*;"
				
				+ "public class NR {"
				+ "	public NR() {}"
				
				+ "	@ParameterSource(\"someid2\")"
				+" private int y= 5;"
				
				+ "	@ParameterSource(\"someid\")"
				+ "	private int doSomething(ComponentEvent obj) {"
				+ " 	return 5;"
				+ " }"
				
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("NR", classContent));
	}

	void compile(String className, String classContent) {
		Processor processor = new BootProcessor();
		CompileOptions options = new CompileOptions().processors(processor);
		Reflect.compile("parametersource." + className, classContent, options).type();
	}

	static void assertExceptionWithMessage(Exception ex, String message) {
		assertTrue(ex.getMessage(), ex.getMessage().toLowerCase().contains(message.toLowerCase()));
	}
}
