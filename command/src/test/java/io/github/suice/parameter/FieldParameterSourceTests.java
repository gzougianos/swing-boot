package io.github.suice.parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.ParameterSource;

@SuppressWarnings("all")
class FieldParameterSourceTests {

	private int x = 5;
	private static String y = "somes";

	@Test
	void main() throws Exception {
		FieldParameterSource source = new FieldParameterSource("id", this.getClass().getDeclaredField("x"));
		assertEquals(5, source.getValue(this, null));
		assertEquals(int.class, source.getValueReturnType());

		FieldParameterSource source2 = new FieldParameterSource("id", this.getClass().getDeclaredField("x"));
		assertEquals(source, source2);

		FieldParameterSource staticS = new FieldParameterSource("id", this.getClass().getDeclaredField("y"));
		assertEquals("somes", staticS.getValue(this, null));
		assertEquals(String.class, staticS.getValueReturnType());
	}

	@Test
	void wrongIds() throws Exception {
		assertThrows(ParameterSourceException.class, () -> new FieldParameterSource(null, this.getClass().getDeclaredField("x")));
		assertThrows(ParameterSourceException.class, () -> new FieldParameterSource("", this.getClass().getDeclaredField("x")));
		assertThrows(ParameterSourceException.class,
				() -> new FieldParameterSource(ParameterSource.THIS, this.getClass().getDeclaredField("x")));
	}

	@Test
	void wrongSourceOwner() throws Exception {
		FieldParameterSource source = new FieldParameterSource("id", this.getClass().getDeclaredField("x"));
		assertThrows(ParameterSourceException.class, () -> source.getValue(new String(), null));
	}
}
