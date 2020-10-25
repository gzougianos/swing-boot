package io.github.suice.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.AWTEvent;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import io.github.suice.command.annotation.ParameterSource;
import io.github.suice.command.exception.InvalidParameterSourceException;

class ParameterSourceScanTests {

	@Test
	void staticField() throws Exception {
		ParameterSourceScan scan = new ParameterSourceScan(StaticField.class);
		assertEquals(1, scan.getParameterSources().size());
		assertEquals(StaticField.class.getDeclaredField("x"), scan.getParameterSources().get("id").getAccessibleObject());
	}

	@Test
	void staticMethod() throws Exception {
		ParameterSourceScan scan = new ParameterSourceScan(StaticMethod.class);
		assertEquals(1, scan.getParameterSources().size());
		assertEquals(StaticMethod.class.getDeclaredMethod("x"), scan.getParameterSources().get("id").getAccessibleObject());
	}

	@Test
	void scansFieldsAndMethods() throws Exception {
		ParameterSourceScan scan = new ParameterSourceScan(MethodAndField.class);
		assertEquals(2, scan.getParameterSources().size());
		assertEquals(MethodAndField.class.getDeclaredMethod("x"),
				scan.getParameterSources().get("idmethod").getAccessibleObject());
		assertEquals(MethodAndField.class.getDeclaredField("x"), scan.getParameterSources().get("idfield").getAccessibleObject());
	}

	@Test
	void exceptionWhenMultipleSourcesSameId() throws Exception {
		assertThrows(InvalidParameterSourceException.class, () -> new ParameterSourceScan(MultipleSourcesSameId.class));
	}

	@Test
	void methodHasNoAwtEventParameter() throws Exception {
		assertThrows(InvalidParameterSourceException.class, () -> new ParameterSourceScan(MethodWithNoAwtEventParameter.class));
	}

	@Test
	void methodHasMoreThanOneParameter() throws Exception {
		assertThrows(InvalidParameterSourceException.class, () -> new ParameterSourceScan(MethodWithMoreThanOneParameter.class));
	}

	@Test
	void methodWithOneAwtParameter() throws Exception {
		ParameterSourceScan scan = new ParameterSourceScan(MethodWithOneAwtEventParameter.class);
		assertEquals(1, scan.getParameterSources().size());
		assertEquals(MethodWithOneAwtEventParameter.class.getDeclaredMethod("x", ComponentEvent.class),
				scan.getParameterSources().get("id").getAccessibleObject());
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

	@SuppressWarnings("serial")
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

}
