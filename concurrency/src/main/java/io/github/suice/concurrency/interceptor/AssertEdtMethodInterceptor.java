package io.github.suice.concurrency.interceptor;

import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.suice.concurrency.AssertEdt;
import io.github.suice.concurrency.exception.MethodExecutedInWrongThreadException;

public class AssertEdtMethodInterceptor implements MethodInterceptor {
	private static final Logger log = LoggerFactory.getLogger(AssertEdtMethodInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (SwingUtilities.isEventDispatchThread())
			return invocation.proceed();

		Method method = invocation.getMethod();
		AssertEdt assertEdt = method.getAnnotation(AssertEdt.class);
		final String errorMessage = "@AssertEdt method '" + method.getName() + "' declared in "
				+ method.getDeclaringClass() + " executed outside the Event Dispatch Thread.";
		if (assertEdt.throwException()) {
			throw new MethodExecutedInWrongThreadException(errorMessage);
		}

		log.warn(errorMessage);
		return invocation.proceed();
	}

}
