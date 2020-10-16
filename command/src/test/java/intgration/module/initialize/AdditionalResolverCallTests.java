package intgration.module.initialize;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.inject.Singleton;
import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.github.suice.command.Command;
import io.github.suice.command.CommandModule;
import io.github.suice.command.InstallCommands;
import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.installer.ComponentAnnotationResolver;

class AdditionalResolverCallTests {

	private CommandModule module;
	private ComponentAnnotationResolver installer;

	@Test
	void nullFieldException() {
		Injector injector = Guice.createInjector(createModuleWith(InitializeCommandsNullField.class), module);

		assertThrows(Exception.class, () -> injector.getInstance(InitializeCommandsNullField.class));
		verifyZeroInteractions(installer);
	}

	@Test
	void notInitializeCommandsClass() {
		Injector injector = Guice.createInjector(createModuleWith(NotInitializeCommands.class), module);

		injector.getInstance(NotInitializeCommands.class);
		verifyZeroInteractions(installer);
	}

	@Test
	void initializeCommandsButFieldWithoutAnnotation() {
		Injector injector = Guice.createInjector(createModuleWith(InitializeCommandsButFieldWithoutAnnotation.class), module);

		injector.getInstance(InitializeCommandsButFieldWithoutAnnotation.class);
		verifyZeroInteractions(installer);
	}

	@Test
	void properInitialization() {
		Injector injector = Guice.createInjector(createModuleWith(ProperInitialization.class), module);

		ProperInitialization intializee = injector.getInstance(ProperInitialization.class);
		verify(installer).install(eq(intializee.button), any());
	}

	@BeforeEach
	void init() {
		module = new CommandModule(getClass().getPackage().getName());
		installer = mock(ComponentAnnotationResolver.class);
		module.addAnnotationResolver(installer);
		when(installer.supports(any())).thenReturn(Boolean.TRUE);
	}

	private Module createModuleWith(Class<?> clazz) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(clazz);
			}
		};
	}

	@Singleton
	@InstallCommands
	private static class InitializeCommandsNullField {

		@OnActionPerformed(TestCommand.class)
		private JButton button;

	}

	@InstallCommands
	private static class InitializeCommandsButFieldWithoutAnnotation {

		@SuppressWarnings("unused")
		private JButton button;

	}

	@InstallCommands
	private static class ProperInitialization {

		@OnActionPerformed(TestCommand.class)
		private JButton button = new JButton();

	}

	@Singleton
	private static class NotInitializeCommands {

		@OnActionPerformed(TestCommand.class)
		private JButton button;

	}

	private static class TestCommand implements Command<Void> {

		@Override
		public void execute(Optional<Void> parameter) {
		}

	}
}
