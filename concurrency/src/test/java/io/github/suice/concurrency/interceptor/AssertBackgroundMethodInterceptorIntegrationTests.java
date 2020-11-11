package io.github.suice.concurrency.interceptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.suice.concurrency.AssertBackground;
import io.github.suice.concurrency.ConcurrencyModule;
import io.github.suice.concurrency.LogFieldValueChanger;
import io.github.suice.concurrency.exception.AssertThreadException;
import testutils.UiExtension;
import testutils.UiTest;

@ExtendWith(UiExtension.class)
class AssertBackgroundMethodInterceptorIntegrationTests {

	@UiTest
	void exceptionWhenRunsInEdt() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatThrowsException instance = injector.getInstance(AssertorThatThrowsException.class);
		assertThrows(AssertThreadException.class, instance::run);
	}

	@Test
	void noExceptionWhenRunOutsideEdt() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatThrowsException instance = injector.getInstance(AssertorThatThrowsException.class);
		assertDoesNotThrow(instance::run);
	}

	@UiTest
	void warningWhenRunsInEdtButDoesNotThrowException() throws Exception {
		Logger logger = mock(Logger.class);
		new LogFieldValueChanger(AssertBackgroundMethodInterceptor.class).replaceWith(logger);

		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatLogsWarning instance = injector.getInstance(AssertorThatLogsWarning.class);
		assertDoesNotThrow(instance::run);

		verify(logger).warn(anyString());
		verifyNoMoreInteractions(logger);
	}

	static class AssertorThatThrowsException {
		@AssertBackground
		void run() {

		}
	}

	static class AssertorThatLogsWarning {
		@AssertBackground(throwException = false)
		void run() {

		}
	}
}
