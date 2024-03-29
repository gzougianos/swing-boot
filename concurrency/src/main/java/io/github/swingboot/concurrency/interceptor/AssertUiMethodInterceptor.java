package io.github.swingboot.concurrency.interceptor;

import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.swingboot.concurrency.AssertUi;
import io.github.swingboot.concurrency.exception.AssertThreadException;

public class AssertUiMethodInterceptor implements MethodInterceptor {
	private static final Logger log = LoggerFactory.getLogger(AssertUiMethodInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (SwingUtilities.isEventDispatchThread())
			return invocation.proceed();

		Method method = invocation.getMethod();
		AssertUi assertUi = method.getAnnotation(AssertUi.class);
		final String errorMessage = "@AssertUi method '" + method.getName() + "' declared in "
				+ method.getDeclaringClass() + " executed outside the Event Dispatch Thread.";

		if (assertUi.throwException()) {
			throw new AssertThreadException(errorMessage);
		}

		log.warn(errorMessage);
		return invocation.proceed();
	}

}
