package io.github.suice.control.module.correctbinding;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import io.github.suice.control.Control;
import io.github.suice.control.ControlModule;
import io.github.suice.control.Controls;
import io.github.suice.control.module.correctbinding.PublicControl.InnerNonStaticControl;
import io.github.suice.control.module.correctbinding.sub.SubpackageControl;

class ControlModuleBindingTests {

	@Test
	void correctBindingsWithoutSubpackageScan() {
		ControlModule module = new ControlModule(PublicControl.class);
		Injector injector = Guice.createInjector(module);

		Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();

		assertNotNull(allBindings.get(Key.get(Controls.class)));
		assertNotNull(allBindings.get(Key.get(PublicControl.class)));
		assertNotNull(allBindings.get(Key.get(PackagePrivateControl.class)));
		assertNull(allBindings.get(Key.get(InnerStaticControl.class)));
		assertNull(allBindings.get(Key.get(InnerNonStaticControl.class)));

		assertNull(allBindings.get(Key.get(AbstractControl.class)));
		assertNull(allBindings.get(Key.get(SubpackageControl.class)));
	}

	@Test
	void correctBindingsWithSubpackageScan() {
		ControlModule module = new ControlModule(PublicControl.class, true);
		Injector injector = Guice.createInjector(module);

		Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();

		assertNotNull(allBindings.get(Key.get(Controls.class)));
		assertNotNull(allBindings.get(Key.get(PublicControl.class)));
		assertNotNull(allBindings.get(Key.get(PackagePrivateControl.class)));
		assertNull(allBindings.get(Key.get(InnerStaticControl.class)));
		assertNull(allBindings.get(Key.get(InnerNonStaticControl.class)));

		assertNull(allBindings.get(Key.get(AbstractControl.class)));
		assertNotNull(allBindings.get(Key.get(SubpackageControl.class)));
	}

	@Singleton
	private static class InnerStaticControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}

	}
}
