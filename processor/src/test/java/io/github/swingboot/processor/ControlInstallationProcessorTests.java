package io.github.swingboot.processor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.processing.Processor;

import org.joor.CompileOptions;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.jupiter.api.Test;

class ControlInstallationProcessorTests {

	@Test
	void allOkWhenInField() {
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				
				+ "public class G {"
				+ " @OnActionPerformed(TestControl.class) "
				+ " private JButton button;"
				
				+ "	public G() {}"
				
				+ " static class TestControl implements Control<Void> {"
				+ "		public void perform(Void v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("G", classContent));
	}

	@Test
	void allOkWhenInClass() {
		//Careful to the import controlinstall.H
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				+ "import controlinstall.H.*;"
				
				+ " @OnActionPerformed(TestControl.class) "
				+ "public class H extends JButton{"
				
				+ "	public H() {}"
				
				+ " static class TestControl implements Control<Void> {"
				+ "		public void perform(Void v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("H", classContent));
	}

	@Test
	void notExtendingATargetWhenIsField() {
		//JPanel does not have add action listener
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				
				+ "public class F {"
				+ " @OnActionPerformed(TestControl.class) "
				+ " private JLabel panel; "
				
				+ "	public F() {}"
				
				+ " static class TestControl implements Control<Void> {"
				+ "		public void perform(Void v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("F", classContent));
		assertExceptionWithMessage(ex, "can be used only");
	}

	@Test
	void notExtendingTargetWhenIsOnClass() {
		//Careful to the import controlinstall.H
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				+ "import controlinstall.Y.*;"
				
				+ " @OnActionPerformed(TestControl.class) "
				+ " public class Y {"
				
				+ "	public Y() {}"
				
				+ " static class TestControl implements Control<Void> {"
				+ "		public void perform(Void v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("Y", classContent));
		assertExceptionWithMessage(ex, "can be used only");
	}

	@Test
	void parameterSourceGivenForNonParameterizedControl() {
		//Careful to the import controlinstall.H
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				+ "import controlinstall.YE.*;"
				
				+ " @OnActionPerformed(value = TestControl.class, parameterSource = \"parId\") "
				+ " public class YE extends JButton {"
				
				+ "	public YE() {}"
				
				+ " @ParameterSource(\"parId\")"
				+ " private int souce() {"
				+ " 	return 5;"
				+ " }"
				
				+ " static class TestControl implements Control<Void> {"
				+ "		public void perform(Void v){} "
				+ "		public void perform2(Void v){} "	
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("YE", classContent));
		assertExceptionWithMessage(ex, "takes no parameters");
	}

	@Test
	void parameterSourceNotGivenForNonNullableParameterizedControl() {
		//Careful to the import controlinstall.H
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				+ "import controlinstall.YEZ.*;"
				
				+ " @OnActionPerformed(value = IntControl.class) "
				+ " public class YEZ extends JButton {"
				
				+ "	public YEZ() {}"
				
				
				+ " static class IntControl implements Control<Integer> {"
				+ "		public void perform(Integer v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("YEZ", classContent));
		assertExceptionWithMessage(ex, "Parameter source not given");
	}

	@Test
	void parameterSourceNotGivenForNullableParameterizedControl() {
		//Careful to the import controlinstall.H
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				+ "import controlinstall.YEG.*;"
				+ "import javax.annotation.*;"
				
				+ " @OnActionPerformed(value = IntControl.class) "
				+ " public class YEG extends JButton {"
				
				+ "	public YEG() {}"
				
				
				+ " static class IntControl implements Control<Integer> {"
				+ "		public void perform(@Nullable Integer v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("YEG", classContent));
	}

	@Test
	void parameterSourceDoesNotExist() {
		//Careful to the import controlinstall.H
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				+ "import controlinstall.L.*;"
				
				+ " @OnActionPerformed(value = IntControl.class, parameterSource = \"parId\") "
				+ " public class L extends JButton{"
				
				+ "	public L() {}"
				
				+ " static class IntControl implements Control<Integer> {"
				+ "		public void perform(Integer v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		ReflectException ex = assertThrows(ReflectException.class, () -> compile("L", classContent));
		assertExceptionWithMessage(ex, "does not exist in class");
	}

	@Test
	void allOkParameterSourceExists() {
		//Careful to the import controlinstall.H
		String classContent =
		//@formatter:off
				"package controlinstall;"

				+ "import io.github.swingboot.control.annotation.*;"
				+ "import io.github.swingboot.control.*;"
				+ "import javax.swing.*;"
				+ "import controlinstall.LE.*;"
				
				+ " @OnActionPerformed(value = IntControl.class, parameterSource = \"parId\") "
				+ " public class LE extends JButton{"
				
				+ "	public LE() {}"
				
				+ " @ParameterSource(\"parId\")"
				+ " private int souce() {"
				+ " 	return 5;"
				+ " }"
				
				+ " static class IntControl implements Control<Integer> {"
				+ "		public void perform(Integer v){} "
				+ " }"
				
				+ "}";
		//@formatter:on
		assertDoesNotThrow(() -> compile("LE", classContent));
	}

	void compile(String className, String classContent) {
		Processor processor = new BootProcessor();
		CompileOptions options = new CompileOptions().processors(processor);
		Reflect.compile("controlinstall." + className, classContent, options).type();
	}

	static void assertExceptionWithMessage(Exception ex, String message) {
		assertTrue(ex.getMessage(), ex.getMessage().toLowerCase().contains(message.toLowerCase()));
	}
}
