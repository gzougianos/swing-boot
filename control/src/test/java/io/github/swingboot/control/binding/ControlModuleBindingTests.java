package io.github.swingboot.control.binding;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Map;

import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.binding.PublicControl.InnerNonStaticControl;
import io.github.swingboot.control.binding.testsubpackage.SubpackageControl;

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

		assertSame(injector.getInstance(PublicControl.class), injector.getInstance(PublicControl.class));

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
