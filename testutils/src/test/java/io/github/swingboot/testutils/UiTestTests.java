package io.github.swingboot.testutils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UiExtension.class)
class UiTestTests {

	@UiTest
	void main() {
		assertTrue(SwingUtilities.isEventDispatchThread());
	}

}
