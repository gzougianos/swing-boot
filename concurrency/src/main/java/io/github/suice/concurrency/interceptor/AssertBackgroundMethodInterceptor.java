package io.github.suice.concurrency.interceptor;

import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.concurrency.AssertBackground;
import io.github.suice.concurrency.exception.AssertThreadException;

public class AssertBackgroundMethodInterceptor implements MethodInterceptor {
	private static final Logger log = LoggerFactory.getLogger(AssertBackgroundMethodInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (!SwingUtilities.isEventDispatchThread())
			return invocation.proceed();

		Method method = invocation.getMethod();
		AssertBackground assertBackground = method.getAnnotation(AssertBackground.class);
		final String errorMessage = "@AssertBackground method '" + method.getName() + "' declared in "
				+ method.getDeclaringClass() + " executed inside the Event Dispatch Thread.";

		if (assertBackground.throwException()) {
			throw new AssertThreadException(errorMessage);
		}

		log.warn(errorMessage);
		return invocation.proceed();
	}

}
