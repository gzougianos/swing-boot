package io.github.swingboot.control;

import static com.google.inject.matcher.Matchers.annotatedWith;

import java.io.IOException;
import java.lang.reflect.Modifier;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import io.github.swingboot.control.annotation.InstallControls;
import io.github.swingboot.control.annotation.WithoutControls;

public class ControlModule extends AbstractModule {
	private static final Logger log = LoggerFactory.getLogger(ControlModule.class);
	private static final Matcher<TypeLiteral<?>> INSTALL_CONTROLS_MATCHER = new InstallControlsMatcher();
	private static ClassPath classpath;
	private final String controlsPackage;
	private InstallControlsInjectionListener installControlsInjectionListener;
	private boolean includeSubpackages;

	public ControlModule(String controlsPackage) {
		this(controlsPackage, false);
	}

	public ControlModule(Class<? extends Control<?>> controlClass, boolean includeSubpackages) {
		this(controlClass.getPackage().getName(), includeSubpackages);
	}

	public ControlModule(Class<? extends Control<?>> controlClass) {
		this(controlClass, false);
	}

	public ControlModule(String controlsPackage, boolean includeSubpackages) {
		this.controlsPackage = controlsPackage;
		this.includeSubpackages = includeSubpackages;
		this.installControlsInjectionListener = new InstallControlsInjectionListener();
	}

	@Override
	protected void configure() {
		initClassPath();
		requestInjection(this);
		bindListener(INSTALL_CONTROLS_MATCHER, new TypeListener() {

			@Override
			public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
				encounter.register(installControlsInjectionListener);
			}
		});

		bind(Controls.class).to(InjectableControls.class).asEagerSingleton();
		bind(ControlInstaller.class).asEagerSingleton();

		bindControls();

		WithoutControlsMethodInterceptor passiveViewInterceptor = new WithoutControlsMethodInterceptor(
				getProvider(ControlInstaller.class));
		bindInterceptor(annotatedWith(InstallControls.class), annotatedWith(WithoutControls.class),
				passiveViewInterceptor);
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("EQWEQWMODULE");
	}

	@SuppressWarnings("unchecked")
	private void bindControls() {
		ImmutableSet<ClassInfo> topLevelClasses = includeSubpackages
				? classpath.getTopLevelClassesRecursive(controlsPackage)
				: classpath.getTopLevelClasses(controlsPackage);

		for (ClassInfo classInfo : topLevelClasses) {
			Class<?> clazz = classInfo.load();
			if (isControl(clazz)) {
				bindControlClass((Class<? extends Control<?>>) clazz);
			}
		}
	}

	protected void bindControlClass(Class<? extends Control<?>> controlType) {
		bind(controlType).in(Singleton.class);
	}

	private boolean isControl(Class<?> clazz) {
		//@formatter:off
		return Control.class.isAssignableFrom(clazz) 
				&& !clazz.equals(Control.class)
				&& !Modifier.isAbstract(clazz.getModifiers()) 
				&& !clazz.isAnonymousClass();
		//@formatter:on
	}

	@Inject
	private void initListener(ControlInstaller controlInstaller) {
		installControlsInjectionListener.setControlInstaller(controlInstaller);
	}

	private void initClassPath() {
		if (classpath != null)
			return;

		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			classpath = ClassPath.from(loader);
		} catch (IOException e) {
			log.error("Error creating classpath.", e);
		}
	}

	private static class InstallControlsMatcher extends AbstractMatcher<TypeLiteral<?>> {

		@Override
		public boolean matches(TypeLiteral<?> t) {
			return t.getRawType().isAnnotationPresent(InstallControls.class);
		}

	}
}
