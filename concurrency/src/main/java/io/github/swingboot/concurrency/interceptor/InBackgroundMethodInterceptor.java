package io.github.swingboot.concurrency.interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.swingboot.concurrency.exception.InBackgroundThreadMethodExecutionException;

public class InBackgroundMethodInterceptor implements MethodInterceptor {
	private static final Logger log = LoggerFactory.getLogger(InBackgroundMethodInterceptor.class);
	private ThreadPoolExecutor executorService;

	public InBackgroundMethodInterceptor(int poolSize) {
		executorService = new BackgroundThreadPoolExecutor(poolSize);
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (!SwingUtilities.isEventDispatchThread())
			return invocation.proceed();

		final Method method = invocation.getMethod();
		final String methodDescription = "method '" + method.getName() + "()' declared in "
				+ method.getDeclaringClass();

		warnIfMethodIsNotVoid(method, methodDescription);

		executorService.submit(() -> {
			try {
				return invocation.proceed();
			} catch (Throwable e) {
				final String errorMessage = "Error executing in a background thread " + methodDescription
						+ ".";
				throw new InBackgroundThreadMethodExecutionException(errorMessage, e);
			}
		});
		return null;
	}

	private void warnIfMethodIsNotVoid(final Method method, final String methodDescription) {
		boolean isVoid = method.getReturnType().equals(Void.TYPE)
				|| method.getReturnType().equals(Void.class);
		if (!isVoid) {
			log.warn(methodDescription + " returns a value. @InBackground methods should be void.");
		}
	}

	private static class BackgroundThreadPoolExecutor extends ThreadPoolExecutor {

		private BackgroundThreadPoolExecutor(int poolSize) {
			super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
					new BackgroundThreadFactory());
		}

		@Override
		protected void afterExecute(final Runnable runnable, Throwable throwable) {
			super.afterExecute(runnable, throwable);
			if (throwable == null && runnable instanceof Future<?>) {
				try {
					Future<?> future = (Future<?>) runnable;
					if (future.isDone()) {
						future.get();
					}
				} catch (CancellationException | InterruptedException ce) {
					throwable = ce;
				} catch (ExecutionException ee) {
					throwable = ee.getCause();
				}
			}
			if (throwable != null)
				throw new RuntimeException(throwable);
		}
	}

	private static class BackgroundThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		private BackgroundThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "background-thread-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}

	}
}
