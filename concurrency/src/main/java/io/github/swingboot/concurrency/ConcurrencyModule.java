package io.github.swingboot.concurrency;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import com.google.inject.AbstractModule;

import io.github.swingboot.concurrency.interceptor.AssertBackgroundMethodInterceptor;
import io.github.swingboot.concurrency.interceptor.AssertUiMethodInterceptor;
import io.github.swingboot.concurrency.interceptor.InBackgroundMethodInterceptor;
import io.github.swingboot.concurrency.interceptor.InUiMethodInterceptor;

public class ConcurrencyModule extends AbstractModule {
	private int backgroundThreadPoolSize = 10;

	public ConcurrencyModule() {
	}

	public void setBackgroundThreadPoolSize(int backgroundThreadPoolSize) {
		this.backgroundThreadPoolSize = backgroundThreadPoolSize;
	}

	@Override
	protected void configure() {
		bindInterceptor(any(), annotatedWith(AssertUi.class), new AssertUiMethodInterceptor());

		bindInterceptor(any(), annotatedWith(AssertBackground.class),
				new AssertBackgroundMethodInterceptor());

		bindInterceptor(any(), annotatedWith(InBackground.class),
				new InBackgroundMethodInterceptor(backgroundThreadPoolSize));

		bindInterceptor(any(), annotatedWith(InUi.class), new InUiMethodInterceptor());
	}
}
