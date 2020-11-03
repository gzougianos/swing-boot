package io.github.suice.control;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.control.annotation.ParameterSource;
import io.github.suice.control.annotation.listener.OnActionPerformed;
import io.github.suice.control.parameter.FieldAndMethodParameterSourceScan;

@SuppressWarnings("all")
@OnActionPerformed(VoidControl2.class)
class ControlDeclarationTests {

	@Inject
	private int x;

	@OnActionPerformed(VoidControl.class)
	private JPanel panel;

	@OnActionPerformed(value = VoidControl.class, id = "someId", parameterSource = "parsource")
	private JButton button;

	@OnActionPerformed(value = IntControl.class, id = "someId", parameterSource = "parsourceString")
	private JButton button2;

	@OnActionPerformed(value = IntControl.class, id = "someId", parameterSource = "parsource")
	private JButton button3;

	@Test
	void notADeclaresControlAnnotation() throws Exception {
		Field field = ControlDeclarationTests.class.getDeclaredField("x");
		assertThrows(ControlDeclarationException.class,
				() -> new ControlDeclaration(field.getAnnotation(Inject.class), field));
	}

	@Test
	void annotationCannotBeInstalledToTargetElement() throws Exception {
		Field field = ControlDeclarationTests.class.getDeclaredField("panel");
		assertThrows(ControlDeclarationException.class,
				() -> new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field));

		assertThrows(ControlDeclarationException.class,
				() -> new ControlDeclaration(this.getClass().getAnnotation(OnActionPerformed.class), this.getClass()));
	}

	@Test
	void allGood() throws Exception {
		Field field = ControlDeclarationTests.class.getDeclaredField("button");
		ControlDeclaration declaration = new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		assertEquals("parsource", declaration.getParameterSourceId());
		assertEquals("someId", declaration.getId());
		assertEquals(Void.class, declaration.getControlParameterType());
		assertEquals(VoidControl.class, declaration.getControlType());
		assertEquals(field, declaration.getTargetElement());
	}

	@Test
	void settingVoidParameterSource() throws Exception {
		Field field = ControlDeclarationTests.class.getDeclaredField("button");
		ControlDeclaration declaration = new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		FieldAndMethodParameterSourceScan scan = new FieldAndMethodParameterSourceScan(this.getClass());
		assertThrows(ControlDeclarationException.class,
				() -> declaration.setParameterSource(scan.getParameterSources().get("parsource")));
	}

	@Test
	void parameterSourceTypeMismatch() throws Exception {
		Field field = ControlDeclarationTests.class.getDeclaredField("button2");
		ControlDeclaration declaration = new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		FieldAndMethodParameterSourceScan scan = new FieldAndMethodParameterSourceScan(this.getClass());
		assertThrows(ControlDeclarationException.class,
				() -> declaration.setParameterSource(scan.getParameterSources().get("parsourceString")));
	}

	@Test
	void parameterSourceIdMismatch() throws Exception {
		Field field = ControlDeclarationTests.class.getDeclaredField("button2");
		ControlDeclaration declaration = new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		FieldAndMethodParameterSourceScan scan = new FieldAndMethodParameterSourceScan(this.getClass());
		assertThrows(ControlDeclarationException.class,
				() -> declaration.setParameterSource(scan.getParameterSources().get("differentId")));
	}

	@Test
	void parameterSourceOk() throws Exception {
		Field field = ControlDeclarationTests.class.getDeclaredField("button3");
		ControlDeclaration declaration = new ControlDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		FieldAndMethodParameterSourceScan scan = new FieldAndMethodParameterSourceScan(this.getClass());
		declaration.setParameterSource(scan.getParameterSources().get("parsource"));
		assertTrue(declaration.getParameterSource().isPresent());
		assertEquals(-1, declaration.getParameterSource().get().getValue(this, null));
	}

	@ParameterSource("parsource")
	private int parSourceInt() {
		return -1;
	}

	@ParameterSource("parsourceString")
	private String parSourceString() {
		return "";
	}

	@ParameterSource("differentId")
	private int diffId() {
		return -1;
	}

	private static class VoidControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}
	}

	private static class IntControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}

class VoidControl2 implements Control<Void> {
	@Override
	public void perform(Void parameter) {
	}
}
