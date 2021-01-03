package testutils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.platform.commons.annotation.Testable;

/**
 * <p>
 * When used on a test class among {@code @ExtendWith(UiExtension.class)}
 * annotation, all test methods will run in the <b>Event Dispatch Thread</b>.
 * The supported methods are:
 * </p>
 * <ul>
 * <li>{@link BeforeAll}</li>
 * <li>{@link AfterAll}</li>
 * <li>{@link BeforeEach}</li>
 * <li>{@link AfterEach}</li>
 * <li>{@link Test}</li>
 * <li>{@link TestTemplate}</li>
 * </ul>
 * 
 * <p>
 * In order to exclude some annotated methods, add use the {@link #exclude()}
 * method.
 * </p>
 * 
 * @see UiExtension
 * @see UiTest
 * 
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
@Testable
public @interface UiAll {

	/**
	 * <p>
	 * Can be used to exclude annotated test methods that should not run in the
	 * Event Dispatch Thread.
	 * </p>
	 * <p>
	 * For example {@code @UiAll(exclude = BeforeAll.class)} will exclude
	 * {@link BeforeAll} method from running in the Event Dispatch Thread.
	 * </p>
	 * 
	 * @return The annotation types to exclude.
	 */
	Class<? extends Annotation>[] exclude() default {};
}
