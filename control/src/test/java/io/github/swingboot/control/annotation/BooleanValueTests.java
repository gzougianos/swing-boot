package io.github.swingboot.control.annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BooleanValueTests {

	@Test
	void test() {
		assertTrue(BooleanValue.FALSE.matches(false));
		assertFalse(BooleanValue.FALSE.matches(true));
		assertTrue(BooleanValue.ANY.matches(false));
		assertTrue(BooleanValue.ANY.matches(true));
	}

}
