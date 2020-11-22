package io.github.suice.control;

import static io.github.suice.control.DeclaresControlAnnotations.ofElement;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.suice.control.annotation.KeyBinding;
import io.github.suice.control.annotation.OnActionPerformed;
import io.github.suice.control.annotation.OnComponentResized;
import io.github.suice.control.annotation.multiple.MultipleKeyBinding;

class DeclaresControlAnnotationsTests {

	@Nested
	class FlattensAnnotation {
		//@formatter:off
		@OnActionPerformed(value = VoidControl.class)
		@OnComponentResized(value = VoidControl.class)
		@MultipleKeyBinding({ 
				@KeyBinding(keyStroke = "F4", value = VoidControl.class),
				@KeyBinding(keyStroke = "F3", value = VoidControl.class),
			})
		@KeyBinding(keyStroke = "F5", value = VoidControl.class)
		//@formatter:on
		private int x;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("x");
			Set<Annotation> result = ofElement(field);
			assertEquals(5, result.size());
			KeyBinding[] bindings = field.getAnnotationsByType(KeyBinding.class);
			assertTrue(result.contains(bindings[0]));
			assertTrue(result.contains(bindings[1]));
			assertTrue(result.contains(field.getAnnotation(OnActionPerformed.class)));
			assertTrue(result.contains(field.getAnnotation(OnComponentResized.class)));
			assertTrue(result.contains(field.getAnnotation(KeyBinding.class)));
		}
	}

	@Nested
	class EliminatesDuplicates {
		//@formatter:off
		@OnActionPerformed(value = VoidControl.class)
		@OnComponentResized(value = VoidControl.class)
		@MultipleKeyBinding({ 
				@KeyBinding(keyStroke = "F3", value = VoidControl.class),
				@KeyBinding(keyStroke = "F3", value = VoidControl.class),
			})
		//@formatter:on
		private int y;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("y");
			Set<Annotation> result = ofElement(field);
			assertEquals(3, result.size());
		}
	}

	//@formatter:off
	@OnActionPerformed(value = VoidControl.class)
	@MultipleKeyBinding({ 
			@KeyBinding(keyStroke = "F4", value = VoidControl.class),
			@KeyBinding(keyStroke = "F3", value = VoidControl.class),
		})
	//@formatter:on
	@Nested
	class WorksWhenIsClass {
		@Test
		void main() throws Exception {
			Set<Annotation> result = ofElement(getClass());
			assertEquals(3, result.size());
		}
	}

	private static class VoidControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}

	}

}
