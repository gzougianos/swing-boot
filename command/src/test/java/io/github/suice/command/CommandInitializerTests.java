package io.github.suice.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.installer.CommandAnnotationInstaller;

class CommandInitializerTests {

	private CommandInitializer commandInitializer;
	private CommandAnnotationInstaller installer;

	@Test
	void nullField() {
		WithNullField objectWithNullField = new WithNullField();

		assertThrows(CommandInitializationException.class, () -> commandInitializer.initializeCommands(objectWithNullField));
		verifyZeroInteractions(installer);
	}

	@Test
	void inheritedInitiation() {
		ChildClass child = new ChildClass();

		commandInitializer.initializeCommands(child);

		verify(installer).installAnnotation(eq(child.getButton()), isA(OnActionPerformed.class));
	}

	@Test
	void notAComponent() {
		NotAComponent notAComponent = new NotAComponent();

		commandInitializer.initializeCommands(notAComponent);
		verifyZeroInteractions(installer);
	}

	@Test
	void zeroAnnotations() {
		ZeroAnnotations zeroAnnotations = new ZeroAnnotations();

		commandInitializer.initializeCommands(zeroAnnotations);
		verifyZeroInteractions(installer);
	}

	@Test
	void properDeclaration() throws NoSuchFieldException, SecurityException {
		ProperDeclaration properDeclaration = new ProperDeclaration();

		commandInitializer.initializeCommands(properDeclaration);
		verify(installer).supportsAnnotation(eq(ProperDeclaration.class.getDeclaredField("button").getDeclaredAnnotations()[0]));
		verify(installer).installAnnotation(eq(properDeclaration.button), isA(OnActionPerformed.class));

		assertThrows(CommandInitializationException.class, () -> commandInitializer.initializeCommands(properDeclaration));
		verifyNoMoreInteractions(installer);
	}

	@BeforeEach
	void init() {
		CommandExecutor executor = mock(CommandExecutor.class);
		commandInitializer = new CommandInitializer(executor);

		installer = mock(CommandAnnotationInstaller.class);
		when(installer.supportsAnnotation(any())).thenReturn(true);

		commandInitializer.addAnnotationInstaller(installer);
	}

	private static class ProperDeclaration {

		@OnActionPerformed(TestCommand.class)
		private JButton button = new JButton();

	}

	private static class WithNullField {

		@OnActionPerformed(TestCommand.class)
		private JButton button;

	}

	private static class ZeroAnnotations {

		@SuppressWarnings("unused")
		private JButton s;

	}

	@InitializeCommands
	private static class ParentClass {
		@OnActionPerformed(TestCommand.class)
		private JButton button = new JButton();

		public JButton getButton() {
			return button;
		}
	}

	private static class ChildClass extends ParentClass {

	}

	private static class NotAComponent {

		@OnActionPerformed(TestCommand.class)
		private String s = "ss";

	}

	private static class TestCommand implements Command<Void> {

		@Override
		public void execute(Optional<Void> parameter) {
		}

	}
}
