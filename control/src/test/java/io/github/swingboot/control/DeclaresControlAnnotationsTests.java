package io.github.swingboot.control;

import static io.github.swingboot.control.DeclaresControlAnnotations.ofElement;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.swingboot.control.annotation.KeyBinding;
import io.github.swingboot.control.annotation.OnActionPerformed;
import io.github.swingboot.control.annotation.OnComponentResized;
import io.github.swingboot.control.annotation.multiple.MultipleKeyBinding;

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
			assertTrue(result.contains(field.getAnnotation(OnActionPerformed.class)));
			assertTrue(result.contains(field.getAnnotation(OnComponentResized.class)));
			assertTrue(result.contains(field.getAnnotation(KeyBinding.class)));
			List<Annotation> annotations = new ArrayList<>(result);
			annotations.remove(field.getAnnotation(OnActionPerformed.class));
			annotations.remove(field.getAnnotation(OnComponentResized.class));
			annotations.remove(field.getAnnotation(KeyBinding.class));

			assertEquals(2, annotations.size());
			KeyBinding nestedBinding1 = (KeyBinding) annotations.get(0);
			//Since no order, it can be either
			assertTrue(nestedBinding1.keyStroke().equals("F4") || nestedBinding1.keyStroke().equals("F3"));

			KeyBinding nestedBinding2 = (KeyBinding) annotations.get(1);
			assertTrue(nestedBinding2.keyStroke().equals("F4") || nestedBinding2.keyStroke().equals("F3"));
			assertNotSame(nestedBinding1, nestedBinding2);
			//assert the nested ones?
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
