package io.github.suice.concurrency.interceptor;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class InUiMethodInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (SwingUtilities.isEventDispatchThread())
			return invocation.proceed();

		AtomicReference<Throwable> throwable = new AtomicReference<>();
		AtomicReference<Object> object = new AtomicReference<>();

		SwingUtilities.invokeAndWait(() -> {
			try {
				object.set(invocation.proceed());
			} catch (Throwable t) {
				throwable.set(t);
			}
		});

		Throwable t = throwable.get();
		if (t != null)
			throw t;

		return object.get();
	}

}
