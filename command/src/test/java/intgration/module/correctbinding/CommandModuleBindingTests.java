package intgration.module.correctbinding;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import intgration.module.correctbinding.PublicCommand.InnerNonStaticCommand;
import intgration.module.correctbinding.sub.SubpackageCommand;
import io.github.suice.command.Command;
import io.github.suice.command.CommandExecutor;
import io.github.suice.command.CommandModule;

class CommandModuleBindingTests {

	@Test
	void correctBindingsWithoutSubpackageScan() {
		CommandModule module = new CommandModule(PublicCommand.class);
		Injector injector = Guice.createInjector(module);

		Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();

		assertNotNull(allBindings.get(Key.get(CommandExecutor.class)));
		assertNotNull(allBindings.get(Key.get(PublicCommand.class)));
		assertNotNull(allBindings.get(Key.get(PackagePrivateCommand.class)));
		assertNull(allBindings.get(Key.get(InnerStaticCommand.class)));
		assertNull(allBindings.get(Key.get(InnerNonStaticCommand.class)));

		assertNull(allBindings.get(Key.get(AbstractCommand.class)));
		assertNull(allBindings.get(Key.get(SubpackageCommand.class)));
	}

	@Test
	void correctBindingsWithSubpackageScan() {
		CommandModule module = new CommandModule(PublicCommand.class, true);
		Injector injector = Guice.createInjector(module);

		Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();

		assertNotNull(allBindings.get(Key.get(CommandExecutor.class)));
		assertNotNull(allBindings.get(Key.get(PublicCommand.class)));
		assertNotNull(allBindings.get(Key.get(PackagePrivateCommand.class)));
		assertNull(allBindings.get(Key.get(InnerStaticCommand.class)));
		assertNull(allBindings.get(Key.get(InnerNonStaticCommand.class)));

		assertNull(allBindings.get(Key.get(AbstractCommand.class)));
		assertNotNull(allBindings.get(Key.get(SubpackageCommand.class)));
	}

	@Singleton
	private static class InnerStaticCommand implements Command<Void> {

		@Override
		public void execute(Optional<Void> parameter) {
		}

	}
}
