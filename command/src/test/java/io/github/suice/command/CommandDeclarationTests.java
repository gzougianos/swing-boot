package io.github.suice.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.exception.InvalidCommandDeclarationException;

class CommandDeclarationTests {

	@Inject
	private int x;

	@OnActionPerformed(TestCommand.class)
	private JPanel panel;

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

	private static class TestCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {
		}
	}
}
