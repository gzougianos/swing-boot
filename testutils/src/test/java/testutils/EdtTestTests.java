package testutils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EdtExtension.class)
class EdtTestTests {

	@EdtTest
	void main() {
		assertTrue(SwingUtilities.isEventDispatchThread());
	}

}
