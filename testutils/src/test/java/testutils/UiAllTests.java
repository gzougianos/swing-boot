package testutils;

import static org.junit.Assert.assertTrue;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UiExtension.class)
@UiAll
class UiAllTests {

	@Test
	void test() {
		assertEdt();
	}

	@BeforeAll
	static void beforeAll() {
		assertEdt();
	}

	@AfterAll
	static void afterAll() {
		assertEdt();
	}

	@BeforeEach
	void beforeEach() {
		assertEdt();
	}

	@AfterEach
	void afterEach() {
		assertEdt();
	}

	private static void assertEdt() {
		assertTrue(SwingUtilities.isEventDispatchThread());
	}

}
