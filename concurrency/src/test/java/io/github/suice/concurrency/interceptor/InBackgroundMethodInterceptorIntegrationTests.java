package io.github.suice.concurrency.interceptor;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.suice.concurrency.ConcurrencyModule;
import io.github.suice.concurrency.InBackground;
import io.github.suice.concurrency.LogFieldValueChanger;
import testutils.UiExtension;
import testutils.UiTest;

@ExtendWith(UiExtension.class)
class InBackgroundMethodInterceptorIntegrationTests {

	@Test
	void proceedsWhenAlreadyInBackground() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		BackgroundMethodOwner instance = injector.getInstance(BackgroundMethodOwner.class);
		instance.doInBackground();
	}

	@UiTest
	void startsABackgroundThread() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		BackgroundMethodOwner instance = injector.getInstance(BackgroundMethodOwner.class);
		instance.doInBackground();
	}

	@UiTest
	void warnWhenMethodIsNotVoid() throws Exception {
		Logger logger = mock(Logger.class);
		new LogFieldValueChanger(InBackgroundMethodInterceptor.class).replaceWith(logger);

		Injector injector = Guice.createInjector(new ConcurrencyModule());
		BackgroundNotVoidMethodOwner instance = injector.getInstance(BackgroundNotVoidMethodOwner.class);
		instance.doInBackground();

		verify(logger).warn(anyString());
		verifyNoMoreInteractions(logger);
	}

	static class BackgroundMethodOwner {

		@InBackground
		void doInBackground() {
			assertFalse(SwingUtilities.isEventDispatchThread());
		}
	}

	static class BackgroundNotVoidMethodOwner {
		@InBackground
		String doInBackground() {
			assertFalse(SwingUtilities.isEventDispatchThread());
			return "";
		}
	}

}
