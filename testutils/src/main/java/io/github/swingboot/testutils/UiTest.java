package io.github.swingboot.testutils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

/**
 * <p>
 * Replaces {@link Test} annotation for test cases that must run in the Event
 * Dispatch Thread. The test container class must be annotated with
 * <b>{@code @ExtendWith(UiExtension.class)}</b>.
 * </p>
 * 
 */
@Retention(RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Testable
@Test
public @interface UiTest {

}
