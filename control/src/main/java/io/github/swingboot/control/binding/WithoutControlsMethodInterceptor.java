package io.github.swingboot.control.binding;

import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Provider;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import io.github.swingboot.control.WithoutControls;
import io.github.swingboot.control.installation.ControlInstaller;

class WithoutControlsMethodInterceptor implements MethodInterceptor {
	private static final ExecutorService backgroundWorker = Executors.newFixedThreadPool(1);
	private Provider<ControlInstaller> installerProvider;

	WithoutControlsMethodInterceptor(Provider<ControlInstaller> installerProvider) {
		this.installerProvider = installerProvider;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object obj = invocation.getThis();

		ControlInstaller controlInstaller = installerProvider.get();
		if (!controlInstaller.installedTo(obj)) {
			return invocation.proceed();
		}

		if (controlInstaller.areAllUninstalledFrom(obj)) {
			return invocation.proceed();
		}

		WithoutControls withoutControls = invocation.getMethod().getAnnotation(WithoutControls.class);
		if (withoutControls.waitUntillAllEventsDispatched())
			waitUntilAllEventsAreDispatched();

		controlInstaller.uninstallFrom(obj);

		Object invocationResult;
		try {
			invocationResult = invocation.proceed();
		} finally {
			if (withoutControls.waitUntillAllEventsDispatched())
				waitUntilAllEventsAreDispatched();

			controlInstaller.reinstallTo(obj);
		}

		return invocationResult;
	}

	private EventQueue eventQueue() {
		return Toolkit.getDefaultToolkit().getSystemEventQueue();
	}

	private void waitUntilAllEventsAreDispatched() {
		final EventQueue eventQueue = eventQueue();
		SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();
		backgroundWorker.submit(() -> {
			while (eventQueue.peekEvent() != null)
				;
			secondaryLoop.exit();
		});
		secondaryLoop.enter();
	}

}
