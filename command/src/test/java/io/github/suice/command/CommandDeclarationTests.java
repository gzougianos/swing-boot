package io.github.suice.command;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.ParameterSource;
import io.github.suice.command.exception.InvalidCommandDeclarationException;
import io.github.suice.parameter.FieldAndMethodParameterSourceScan;

@SuppressWarnings("all")
class CommandDeclarationTests {

	@Inject
	private int x;

	@OnActionPerformed(VoidCommand.class)
	private JPanel panel;

	@OnActionPerformed(value = VoidCommand.class, id = "someId", parameterSource = "parsource")
	private JButton button;

	@OnActionPerformed(value = IntCommand.class, id = "someId", parameterSource = "parsourceString")
	private JButton button2;

	@OnActionPerformed(value = IntCommand.class, id = "someId", parameterSource = "parsource")
	private JButton button3;

	@Test
	void notADeclaresCommandAnnotation() throws Exception {
		Field field = CommandDeclarationTests.class.getDeclaredField("x");
		assertThrows(InvalidCommandDeclarationException.class,
				() -> new CommandDeclaration(field.getAnnotation(Inject.class), field));
	}

	@Test
	void annotationCannotBeInstalledToTargetElement() throws Exception {
		Field field = CommandDeclarationTests.class.getDeclaredField("panel");
		assertThrows(InvalidCommandDeclarationException.class,
				() -> new CommandDeclaration(field.getAnnotation(OnActionPerformed.class), field));
	}

	@Test
	void allGood() throws Exception {
		Field field = CommandDeclarationTests.class.getDeclaredField("button");
		CommandDeclaration declaration = new CommandDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		assertEquals("parsource", declaration.getParameterSourceId());
		assertEquals("someId", declaration.getId());
		assertEquals(Void.class, declaration.getCommandGenericParameterType());
		assertEquals(VoidCommand.class, declaration.getCommandType());
		assertEquals(field, declaration.getTargetElement());
	}

	@Test
	void settingVoidParameterSource() throws Exception {
		Field field = CommandDeclarationTests.class.getDeclaredField("button");
		CommandDeclaration declaration = new CommandDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		FieldAndMethodParameterSourceScan scan = new FieldAndMethodParameterSourceScan(this.getClass());
		assertThrows(InvalidCommandDeclarationException.class,
				() -> declaration.setParameterSource(scan.getParameterSources().get("parsource")));
	}

	@Test
	void parameterSourceTypeMismatch() throws Exception {
		Field field = CommandDeclarationTests.class.getDeclaredField("button2");
		CommandDeclaration declaration = new CommandDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		FieldAndMethodParameterSourceScan scan = new FieldAndMethodParameterSourceScan(this.getClass());
		assertThrows(InvalidCommandDeclarationException.class,
				() -> declaration.setParameterSource(scan.getParameterSources().get("parsourceString")));
	}

	@Test
	void parameterSourceIdMismatch() throws Exception {
		Field field = CommandDeclarationTests.class.getDeclaredField("button2");
		CommandDeclaration declaration = new CommandDeclaration(field.getAnnotation(OnActionPerformed.class), field);
		FieldAndMethodParameterSourceScan scan = new FieldAndMethodParameterSourceScan(this.getClass());
		assertThrows(InvalidCommandDeclarationException.class,
				() -> declaration.setParameterSource(scan.getParameterSources().get("differentId")));
	}

	@Test
	void parameterSourceOk() throws Exception {
		Field field = CommandDeclarationTests.class.getDeclaredField("button3");
		CommandDeclaration declaration = new CommandDeclaration(field.getAnnotation(OnActionPerformed.class), field);
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

	private static class VoidCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {
		}
	}

	private static class IntCommand implements Command<Integer> {
		@Override
		public void execute(Integer parameter) {
		}
	}
}
