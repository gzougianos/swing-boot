package io.github.swingboot.concurrency.interceptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.swingboot.concurrency.AssertUi;
import io.github.swingboot.concurrency.ConcurrencyModule;
import io.github.swingboot.concurrency.LogFieldValueChanger;
import io.github.swingboot.concurrency.exception.AssertThreadException;
import io.github.swingboot.testutils.UiExtension;
import io.github.swingboot.testutils.UiTest;

@ExtendWith(UiExtension.class)
public class AssertUiMethodInterceptorIntegrationTests {
	@Test
	void exceptionWhenNotInEdt() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatThrowsException instance = injector.getInstance(AssertorThatThrowsException.class);
		assertThrows(AssertThreadException.class, instance::run);
	}

	@Test
	void logMessageWhenNotInEdtAndNoExceptionThrow() throws Exception {
		Logger testLogger = mock(Logger.class);
		new LogFieldValueChanger(AssertUiMethodInterceptor.class).replaceWith(testLogger);

		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatDoesNotThrowException instance = injector
				.getInstance(AssertorThatDoesNotThrowException.class);
		assertDoesNotThrow(instance::run);

		verify(testLogger).warn(any());
		verifyNoMoreInteractions(testLogger);
	}

	@UiTest
	void inEdt() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatThrowsException instance = injector.getInstance(AssertorThatThrowsException.class);
		assertDoesNotThrow(instance::run);
	}

	@UiTest
	void inEdt2() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatDoesNotThrowException instance = injector
				.getInstance(AssertorThatDoesNotThrowException.class);
		assertDoesNotThrow(instance::run);
	}

	static class AssertorThatThrowsException {
		@AssertUi
		void run() {

		}
	}

	static class AssertorThatDoesNotThrowException {
		@AssertUi(throwException = false)
		void run() {

		}
	}

}
