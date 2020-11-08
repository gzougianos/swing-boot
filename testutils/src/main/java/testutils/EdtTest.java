package testutils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Retention(RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Testable
@Test
public @interface EdtTest {

}
