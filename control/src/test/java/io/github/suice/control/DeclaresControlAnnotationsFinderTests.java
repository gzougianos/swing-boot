package io.github.suice.control;

import static io.github.suice.control.DeclaresControlAnnotationsFinder.find;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import integration.example.IncreaseClickCounterControl;
import io.github.suice.control.annotation.KeyBinding;
import io.github.suice.control.annotation.MultipleKeyBinding;
import io.github.suice.control.annotation.OnActionPerformed;
import io.github.suice.control.annotation.OnComponentResized;
import io.github.suice.control.annotation.ParameterSource;

class DeclaresControlAnnotationsFinderTests {

	@Nested
	class FlattensAnnotation {
		//@formatter:off
		@OnActionPerformed(value = IncreaseClickCounterControl.class, parameterSource = ParameterSource.THIS)
		@OnComponentResized(value = IncreaseClickCounterControl.class, parameterSource = ParameterSource.THIS)
		@MultipleKeyBinding({ 
				@KeyBinding(keyStroke = "F4", value = IncreaseClickCounterControl.class),
				@KeyBinding(keyStroke = "F3", value = IncreaseClickCounterControl.class),
			})
		//@formatter:on
		private int x;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("x");
			Set<Annotation> result = find(field);
			assertEquals(4, result.size());
			KeyBinding[] bindings = field.getAnnotationsByType(KeyBinding.class);
			assertTrue(result.contains(bindings[0]));
			assertTrue(result.contains(bindings[1]));
			assertTrue(result.contains(field.getAnnotation(OnActionPerformed.class)));
			assertTrue(result.contains(field.getAnnotation(OnComponentResized.class)));
		}
	}

	@Nested
	class EliminatesDuplicates {
		//@formatter:off
		@OnActionPerformed(value = IncreaseClickCounterControl.class, parameterSource = ParameterSource.THIS)
		@OnComponentResized(value = IncreaseClickCounterControl.class, parameterSource = ParameterSource.THIS)
		@MultipleKeyBinding({ 
				@KeyBinding(keyStroke = "F3", value = IncreaseClickCounterControl.class),
				@KeyBinding(keyStroke = "F3", value = IncreaseClickCounterControl.class),
			})
		//@formatter:on
		private int y;

		@Test
		void main() throws Exception {
			Field field = getClass().getDeclaredField("y");
			Set<Annotation> result = find(field);
			assertEquals(3, result.size());
		}
	}

	//@formatter:off
	@OnActionPerformed(value = IncreaseClickCounterControl.class)
	@MultipleKeyBinding({ 
			@KeyBinding(keyStroke = "F4", value = IncreaseClickCounterControl.class),
			@KeyBinding(keyStroke = "F3", value = IncreaseClickCounterControl.class),
		})
	//@formatter:on
	@Nested
	class WorksWhenIsClass {
		@Test
		void main() throws Exception {
			Set<Annotation> result = find(getClass());
			assertEquals(3, result.size());
		}
	}

}
