package io.github.swingboot.control.parameter;

import static io.github.swingboot.control.parameter.FieldAndMethodParameterSourceScan.of;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.AWTEvent;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.ParameterSource;

@SuppressWarnings("all")
class FieldAndMethodParameterSourceScanTests {

	@Test
	void cache() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(StaticField.class);
		FieldAndMethodParameterSourceScan scan2 = of(StaticField.class);
		assertSame(scan, scan2);
	}

	@Test
	void staticField() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(StaticField.class);
		assertEquals(1, scan.getParameterSources().size());

		FieldParameterSource expected = new FieldParameterSource("id",
				StaticField.class.getDeclaredField("x"));
		assertEquals(expected, scan.getParameterSources().get("id"));
	}

	@Test
	void staticMethod() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(StaticMethod.class);
		assertEquals(1, scan.getParameterSources().size());

		MethodParameterSource expected = new MethodParameterSource("id",
				StaticMethod.class.getDeclaredMethod("x"));
		assertEquals(expected, scan.getParameterSources().get("id"));
	}

	@Test
	void scansFieldsAndMethods() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(MethodAndField.class);
		assertEquals(2, scan.getParameterSources().size());

		MethodParameterSource expectedMethod = new MethodParameterSource("idmethod",
				MethodAndField.class.getDeclaredMethod("x"));
		assertEquals(expectedMethod, scan.getParameterSources().get("idmethod"));

		FieldParameterSource expectedField = new FieldParameterSource("idfield",
				MethodAndField.class.getDeclaredField("x"));
		assertEquals(expectedField, scan.getParameterSources().get("idfield"));
	}

	@Test
	void exceptionWhenMultipleSourcesSameId() throws Exception {
		assertThrows(InvalidParameterSourceException.class, () -> of(MultipleSourcesSameId.class));
	}

	@Test
	void methodHasNoAwtEventParameter() throws Exception {
		assertThrows(InvalidParameterSourceException.class, () -> of(MethodWithNoAwtEventParameter.class));
	}

	@Test
	void methodHasMoreThanOneParameter() throws Exception {
		assertThrows(InvalidParameterSourceException.class, () -> of(MethodWithMoreThanOneParameter.class));
	}

	@Test
	void emptyStringId() throws Exception {
		assertThrows(InvalidParameterSourceException.class, () -> of(EmptyStringAsId.class));
	}

	@Test
	void methodWithOneAwtParameter() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(MethodWithOneAwtEventParameter.class);
		assertEquals(1, scan.getParameterSources().size());

		MethodParameterSource expectedMethod = new MethodParameterSource("id",
				MethodWithOneAwtEventParameter.class.getDeclaredMethod("x", ComponentEvent.class));
		assertEquals(expectedMethod, scan.getParameterSources().get("id"));
	}

	@Test
	void inheritAll() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(ChildWithAll.class);
		assertEquals(3, scan.getParameterSources().size());

		MethodParameterSource expectedMethod = new MethodParameterSource("idmethod",
				MethodAndField.class.getDeclaredMethod("x"));
		assertEquals(expectedMethod, scan.getParameterSources().get("idmethod"));

		FieldParameterSource expectedFieldParent = new FieldParameterSource("idfield",
				MethodAndField.class.getDeclaredField("x"));
		assertEquals(expectedFieldParent, scan.getParameterSources().get("idfield"));

		FieldParameterSource expectedField = new FieldParameterSource("extra",
				ChildWithAll.class.getDeclaredField("x"));
		assertEquals(expectedField, scan.getParameterSources().get("extra"));
	}

	@Test
	void inheritButIgnoreOne() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(ChildIgnoringOne.class);
		assertEquals(1, scan.getParameterSources().size());

		FieldParameterSource expected = new FieldParameterSource("idfield",
				MethodAndField.class.getDeclaredField("x"));
		assertEquals(expected, scan.getParameterSources().get("idfield"));
	}

	@Test
	void inheritButIgnoreAll() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(ChildIgnoreAll.class);
		assertEquals(0, scan.getParameterSources().size());
	}

	@Test
	void methodOverride() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(MethodOverride.class);
		assertEquals(2, scan.getParameterSources().size());

		FieldParameterSource expectedMethodParent = new FieldParameterSource("idfield",
				MethodAndField.class.getDeclaredField("x"));
		assertEquals(expectedMethodParent, scan.getParameterSources().get("idfield"));

		MethodParameterSource expectedMethod = new MethodParameterSource("idmethod",
				MethodOverride.class.getDeclaredMethod("x"));
		assertEquals(expectedMethod, scan.getParameterSources().get("idmethod"));
	}

	@Test
	void fieldOverride() throws Exception {
		FieldAndMethodParameterSourceScan scan = of(FieldOverride.class);
		assertEquals(2, scan.getParameterSources().size());

		MethodParameterSource expectedMethodParent = new MethodParameterSource("idmethod",
				MethodAndField.class.getDeclaredMethod("x"));
		assertEquals(expectedMethodParent, scan.getParameterSources().get("idmethod"));

		MethodParameterSource expectedMethod = new MethodParameterSource("idfield",
				FieldOverride.class.getDeclaredMethod("x"));
		assertEquals(expectedMethod, scan.getParameterSources().get("idfield"));
	}

	private static class StaticField {
		@ParameterSource("id")
		private static final int x = 5;
	}

	private static class StaticMethod {
		@ParameterSource("id")
		private static int x() {
			return -1;
		}
	}

	@InstallControls
	private static class MethodAndField extends JPanel {
		@ParameterSource("idfield")
		private final int x = 5;
		private int y;//show that his is not added
		@Inject
		private int z; //Show that fields with other annotations are dodged

		@Inject //Show that methods with other annotations are dodged
		private void something() {
		}

		@ParameterSource("idmethod")
		private int x() {
			return -1;
		}
	}

	private static class MultipleSourcesSameId {
		@ParameterSource("id")
		private final int x = 5;

		@ParameterSource("id")
		private int x() {
			return -1;
		}
	}

	private static class MethodWithNoAwtEventParameter {
		@ParameterSource("id")
		private int x(int k) {
			return -1;
		}
	}

	private static class MethodWithMoreThanOneParameter {
		@ParameterSource("id")
		private int x(AWTEvent e, int k) {
			return -1;
		}
	}

	private static class MethodWithOneAwtEventParameter {
		@ParameterSource("id")
		private int x(ComponentEvent e) {
			return -1;
		}
	}

	private static class EmptyStringAsId {
		@ParameterSource("")
		private int x(ComponentEvent e) {
			return -1;
		}
	}

	@InstallControls
	private static class ChildWithAll extends MethodAndField {
		@ParameterSource("extra")
		private int x;
	}

	@InstallControls(ignoreIdsFromParent = "idmethod")
	private static class ChildIgnoringOne extends MethodAndField {
	}

	@InstallControls(ignoreAllIdsFromParent = true)
	private static class ChildIgnoreAll extends MethodAndField {
	}

	private static class MethodOverride extends MethodAndField {
		@ParameterSource("idmethod")
		private int x() {
			return -5;
		}
	}

	private static class FieldOverride extends MethodAndField {
		@ParameterSource("idfield")
		private int x() {
			return -5;
		}
	}

}
