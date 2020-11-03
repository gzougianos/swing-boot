package io.github.suice.control;

import java.io.IOException;
import java.lang.reflect.Modifier;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class ControlModule extends AbstractModule {
	private static final Logger log = LoggerFactory.getLogger(ControlModule.class);
	private static final Matcher<TypeLiteral<?>> INSTALL_CONTROLS_MATCHER = new InstallControlsMatcher();
	private ClassPath classpath;
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

	}

	private void bindControls() {
		ImmutableSet<ClassInfo> topLevelClasses = includeSubpackages ? classpath.getTopLevelClassesRecursive(controlsPackage)
				: classpath.getTopLevelClasses(controlsPackage);

		for (ClassInfo classInfo : topLevelClasses) {
			Class<?> clazz = classInfo.load();
			if (isControl(clazz))
				bind(clazz);
		}
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
	private void initListener(ControlInstaller controlInstaller, Injector injector) {
		installControlsInjectionListener.setControlInstaller(controlInstaller);
	}

	private void initClassPath() {
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
