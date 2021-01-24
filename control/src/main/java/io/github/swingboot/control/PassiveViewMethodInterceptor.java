package io.github.swingboot.control;

import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;

import javax.inject.Provider;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

class PassiveViewMethodInterceptor implements MethodInterceptor {

	private Provider<ControlInstaller> installerProvider;

	PassiveViewMethodInterceptor(Provider<ControlInstaller> installerProvider) {
		this.installerProvider = installerProvider;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object obj = invocation.getThis();

		waitUntilAllEventsAreDispatched();

		ControlInstaller controlInstaller = installerProvider.get();
		controlInstaller.uninstallFrom(obj);

		Object o = invocation.proceed();

		waitUntilAllEventsAreDispatched();

		controlInstaller.reinstallTo(obj);

		return o;
	}

	private EventQueue eventQueue() {
		return Toolkit.getDefaultToolkit().getSystemEventQueue();
	}

	private void waitUntilAllEventsAreDispatched() {
		final EventQueue eventQueue = eventQueue();
		SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();
		new Thread(() -> {
			while (eventQueue.peekEvent() != null)
				;
			secondaryLoop.exit();
		}).start();
		secondaryLoop.enter();
	}

}
