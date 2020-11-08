package testutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

public class EdtExtension implements InvocationInterceptor {

	@Override
	public void interceptAfterAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
			ExtensionContext extensionContext) throws Throwable {
		if (includedInEdtAll(invocationContext)) {
			runInEdt(() -> InvocationInterceptor.super.interceptAfterAllMethod(invocation, invocationContext, extensionContext));
		} else {
			InvocationInterceptor.super.interceptAfterAllMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptAfterEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
			ExtensionContext extensionContext) throws Throwable {
		if (includedInEdtAll(invocationContext)) {
			runInEdt(() -> InvocationInterceptor.super.interceptAfterEachMethod(invocation, invocationContext, extensionContext));
		} else {
			InvocationInterceptor.super.interceptAfterEachMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptBeforeAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
			ExtensionContext extensionContext) throws Throwable {
		if (includedInEdtAll(invocationContext)) {
			runInEdt(() -> InvocationInterceptor.super.interceptBeforeAllMethod(invocation, invocationContext, extensionContext));
		} else {
			InvocationInterceptor.super.interceptBeforeAllMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptBeforeEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
			ExtensionContext extensionContext) throws Throwable {
		if (includedInEdtAll(invocationContext)) {
			runInEdt(
					() -> InvocationInterceptor.super.interceptBeforeEachMethod(invocation, invocationContext, extensionContext));
		} else {
			InvocationInterceptor.super.interceptBeforeEachMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
			ExtensionContext extensionContext) throws Throwable {
		final boolean isEdtTest = invocationContext.getExecutable().isAnnotationPresent(EdtTest.class);

		if (isEdtTest || includedInEdtAll(invocationContext)) {
			runInEdt(() -> InvocationInterceptor.super.interceptTestMethod(invocation, invocationContext, extensionContext));
		} else {
			InvocationInterceptor.super.interceptTestMethod(invocation, invocationContext, extensionContext);
		}
	}

	@Override
	public void interceptTestTemplateMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
			ExtensionContext extensionContext) throws Throwable {
		if (includedInEdtAll(invocationContext)) {
			runInEdt(() -> InvocationInterceptor.super.interceptTestTemplateMethod(invocation, invocationContext,
					extensionContext));
		} else {
			InvocationInterceptor.super.interceptTestTemplateMethod(invocation, invocationContext, extensionContext);
		}
	}

	private boolean includedInEdtAll(ReflectiveInvocationContext<?> invocationContext) {
		Executable executable = invocationContext.getExecutable();

		Class<?> declaringClass = executable.getDeclaringClass();
		if (declaringClass.isAnnotationPresent(EdtAll.class)) {
			EdtAll edtAll = declaringClass.getAnnotation(EdtAll.class);
			Set<Class<? extends Annotation>> exlusions = getEdtAllExclusions(edtAll);
			//@formatter:off 
			return !Arrays.asList(executable.getAnnotations()).stream()
					.map(Annotation::annotationType)
					.anyMatch(exlusions::contains);
			//@formatter:on
		}
		return false;
	}

	private Set<Class<? extends Annotation>> getEdtAllExclusions(EdtAll edtAll) {
		Class<? extends Annotation>[] exclusions = edtAll.exclude();
		Set<Class<? extends Annotation>> exclusionsSet = new HashSet<>();
		for (Class<? extends Annotation> exclusion : exclusions) {
			exclusionsSet.add(exclusion);
		}
		return exclusionsSet;
	}

	private static interface Thrower {
		void run() throws Throwable;
	}

	private void runInEdt(Thrower thrower) throws Throwable {
		AtomicReference<Throwable> throwable = new AtomicReference<>();

		SwingUtilities.invokeAndWait(() -> {
			try {
				thrower.run();
			} catch (Throwable e) {
				throwable.set(e);
			}
		});

		Throwable t = throwable.get();
		if (t != null) {
			throw t;
		}
	}
}
