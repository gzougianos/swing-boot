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

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.installer.ComponentAnnotationInstaller;

class CommandInstallerTests {

	private CommandInstaller commandInstaller;
	private ComponentAnnotationInstaller annotationResolver;

	@Test
	void nullField() {
		WithNullField objectWithNullField = new WithNullField();

		assertThrows(CommandInstallationException.class, () -> commandInstaller.installCommands(objectWithNullField));
		verifyZeroInteractions(annotationResolver);
	}

	@Test
	void additionalCommandsInstall() {
		AdditionalCommandInstallation installer = mock(AdditionalCommandInstallation.class);

		commandInstaller.installCommands(installer);

		verify(installer).installCommands(isA(CommandExecutor.class));
		verifyNoMoreInteractions(installer);
	}

	@Test
	void inheritedInstallation() {
		ChildClass child = new ChildClass();

		commandInstaller.installCommands(child);

		verify(annotationResolver).install(eq(child.getButton()), isA(OnActionPerformed.class));
	}

	@Test
	void notAComponent() {
		NotAComponent notAComponent = new NotAComponent();

		commandInstaller.installCommands(notAComponent);
		verifyZeroInteractions(annotationResolver);
	}

	@Test
	void zeroAnnotations() {
		ZeroAnnotations zeroAnnotations = new ZeroAnnotations();

		commandInstaller.installCommands(zeroAnnotations);
		verifyZeroInteractions(annotationResolver);
	}

	@Test
	void properDeclaration() throws NoSuchFieldException, SecurityException {
		ProperDeclaration properDeclaration = new ProperDeclaration();

		commandInstaller.installCommands(properDeclaration);
		verify(annotationResolver).supports(eq(ProperDeclaration.class.getDeclaredField("button").getDeclaredAnnotations()[0]));
		verify(annotationResolver).install(eq(properDeclaration.button), isA(OnActionPerformed.class));

		assertThrows(CommandInstallationException.class, () -> commandInstaller.installCommands(properDeclaration));
		verifyNoMoreInteractions(annotationResolver);
	}

	@Test
	void onComponentObjectWithoutAnnotationInheritance() {
		JButton button = new ParentComponentClass();
		commandInstaller.installCommands(button);

		OnActionPerformed expectedAnnotation = ParentComponentClass.class.getAnnotation(OnActionPerformed.class);
		verify(annotationResolver).supports(eq(expectedAnnotation));
		verify(annotationResolver).install(eq(button), eq(expectedAnnotation));
		verifyNoMoreInteractions(annotationResolver);
	}

	@Test
	void onComponentObjectWithAnnotationInheritance() {
		JButton button = new ChildComponentClass();
		commandInstaller.installCommands(button);

		verifyZeroInteractions(annotationResolver);
	}

	@BeforeEach
	void init() {
		CommandExecutor executor = mock(CommandExecutor.class);
		commandInstaller = new CommandInstaller(executor);

		annotationResolver = mock(ComponentAnnotationInstaller.class);
		when(annotationResolver.supports(any())).thenReturn(true);

		commandInstaller.addAnnotationInstaller(annotationResolver);
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

	@InstallCommands
	private static class ParentClass {
		@OnActionPerformed(TestCommand.class)
		private JButton button = new JButton();

		public JButton getButton() {
			return button;
		}
	}

	@SuppressWarnings("serial")
	@InstallCommands
	@OnActionPerformed(TestCommand.class)
	private static class ParentComponentClass extends JButton {
	}

	@SuppressWarnings("serial")
	private static class ChildComponentClass extends ParentComponentClass {
	}

	private static class ChildClass extends ParentClass {
	}

	private static class NotAComponent {
		@OnActionPerformed(TestCommand.class)
		private String s = "ss";
	}

	private static class TestCommand implements Command<Void> {
		@Override
		public void execute(Void parameter) {
		}
	}
}
