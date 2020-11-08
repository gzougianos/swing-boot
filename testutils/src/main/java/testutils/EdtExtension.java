package testutils;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

public class EdtExtension implements InvocationInterceptor {
	private Throwable throwable;

	@Override
	public void interceptAfterAllMethod(Invocation<Void> invocation,
			ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		if (isOnEdtAnnonated(invocationContext)) {
			runOnEdt(() -> InvocationInterceptor.super.interceptAfterAllMethod(invocation, invocationContext,
					extensionContext));
		} else {
			InvocationInterceptor.super.interceptAfterAllMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptAfterEachMethod(Invocation<Void> invocation,
			ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		if (isOnEdtAnnonated(invocationContext)) {
			runOnEdt(() -> InvocationInterceptor.super.interceptAfterEachMethod(invocation, invocationContext,
					extensionContext));
		} else {
			InvocationInterceptor.super.interceptAfterEachMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptBeforeAllMethod(Invocation<Void> invocation,
			ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		if (isOnEdtAnnonated(invocationContext)) {
			runOnEdt(() -> InvocationInterceptor.super.interceptBeforeAllMethod(invocation, invocationContext,
					extensionContext));
		} else {
			InvocationInterceptor.super.interceptBeforeAllMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptBeforeEachMethod(Invocation<Void> invocation,
			ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		if (isOnEdtAnnonated(invocationContext)) {
			runOnEdt(() -> InvocationInterceptor.super.interceptBeforeEachMethod(invocation, invocationContext,
					extensionContext));
		} else {
			InvocationInterceptor.super.interceptBeforeEachMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
			ExtensionContext extensionContext) throws Throwable {
		if (isOnEdtAnnonated(invocationContext)) {
			runOnEdt(() -> InvocationInterceptor.super.interceptTestMethod(invocation, invocationContext,
					extensionContext));
		} else {
			InvocationInterceptor.super.interceptTestMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptTestTemplateMethod(Invocation<Void> invocation,
			ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
		if (isOnEdtAnnonated(invocationContext)) {
			runOnEdt(() -> InvocationInterceptor.super.interceptTestTemplateMethod(invocation, invocationContext,
					extensionContext));
		} else {
			InvocationInterceptor.super.interceptTestTemplateMethod(invocation, invocationContext, extensionContext);
		}
	}

	private boolean isOnEdtAnnonated(ReflectiveInvocationContext<?> invocationContext) {
		Executable executable = invocationContext.getExecutable();
		return executable.isAnnotationPresent(EdtTest.class)
				|| executable.getDeclaringClass().isAnnotationPresent(EdtTest.class);
	}

	private static interface Thrower {
		void run() throws Throwable;
	}

	private void runOnEdt(Thrower thrower) throws Throwable {
		throwable = null;
		SwingUtilities.invokeAndWait(() -> {
			try {
				thrower.run();
			} catch (Throwable e) {
				throwable = e;
			}
		});
		if (throwable != null)
			throw throwable;
	}
}
