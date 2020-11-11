package io.github.suice.concurrency.interceptor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.suice.concurrency.ConcurrencyModule;
import io.github.suice.concurrency.InUi;
import testutils.EdtExtension;
import testutils.EdtTest;

@ExtendWith(EdtExtension.class)
class InUiMethodInterceptorIntegrationTests {

	@Test
	void startingFromBackground() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		UiMethodOwner instance = injector.getInstance(UiMethodOwner.class);
		assertEquals("something", instance.run());
	}

	@EdtTest
	void startingFromEdt() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		UiMethodOwner instance = injector.getInstance(UiMethodOwner.class);
		assertEquals("something", instance.run());
	}

	@Test
	void makeSureTheExceptionIsThrown() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		UiMethodOwner instance = injector.getInstance(UiMethodOwner.class);
		assertThrows(RuntimeException.class, instance::throwException);
	}

	static class UiMethodOwner {
		@InUi
		String run() {
			assertTrue(SwingUtilities.isEventDispatchThread());
			return "something";
		}

		@InUi
		void throwException() {
			throw new RuntimeException();
		}
	}
}
