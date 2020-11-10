package io.github.suice.concurrency;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import com.google.inject.AbstractModule;

import io.github.suice.concurrency.interceptor.AssertBackgroundMethodInterceptor;
import io.github.suice.concurrency.interceptor.AssertEdtMethodInterceptor;

public class ConcurrencyModule extends AbstractModule {
	public ConcurrencyModule() {
	}

	@Override
	protected void configure() {
		bindInterceptor(any(), annotatedWith(AssertEdt.class), new AssertEdtMethodInterceptor());
		bindInterceptor(any(), annotatedWith(AssertBackground.class), new AssertBackgroundMethodInterceptor());
	}
}
