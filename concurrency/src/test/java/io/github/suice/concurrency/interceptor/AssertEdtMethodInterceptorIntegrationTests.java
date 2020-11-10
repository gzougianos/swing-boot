package io.github.suice.concurrency.interceptor;

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

import io.github.suice.concurrency.AssertEdt;
import io.github.suice.concurrency.ConcurrencyModule;
import io.github.suice.concurrency.LogFieldValueChanger;
import io.github.suice.concurrency.exception.MethodExecutedInWrongThreadException;
import testutils.EdtExtension;
import testutils.EdtTest;

@ExtendWith(EdtExtension.class)
public class AssertEdtMethodInterceptorIntegrationTests {
	@Test
	void exceptionWhenNotInEdt() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatThrowsException instance = injector.getInstance(AssertorThatThrowsException.class);
		assertThrows(MethodExecutedInWrongThreadException.class, instance::run);
	}

	@Test
	void logMessageWhenNotInEdtAndNoExceptionThrow() throws Exception {
		Logger testLogger = mock(Logger.class);
		new LogFieldValueChanger(AssertEdtMethodInterceptor.class).replaceWith(testLogger);

		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatDoesNotThrowException instance = injector.getInstance(AssertorThatDoesNotThrowException.class);
		assertDoesNotThrow(instance::run);

		verify(testLogger).warn(any());
		verifyNoMoreInteractions(testLogger);
	}

	@EdtTest
	void inEdt() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatThrowsException instance = injector.getInstance(AssertorThatThrowsException.class);
		assertDoesNotThrow(instance::run);
	}

	@EdtTest
	void inEdt2() {
		Injector injector = Guice.createInjector(new ConcurrencyModule());
		AssertorThatDoesNotThrowException instance = injector.getInstance(AssertorThatDoesNotThrowException.class);
		assertDoesNotThrow(instance::run);
	}

	static class AssertorThatThrowsException {
		@AssertEdt
		void run() {

		}
	}

	static class AssertorThatDoesNotThrowException {
		@AssertEdt(throwException = false)
		void run() {

		}
	}

}
