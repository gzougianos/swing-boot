package io.github.swingboot.control.parameter;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;

import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.swingboot.control.ParameterSource;
import io.github.swingboot.control.reflect.ReflectionException;

@SuppressWarnings("all")
class MethodParameterSourceTests {
	private AWTEvent injectedAwtEvent;

	@Test
	void wrongIds() throws Exception {
		Method method = this.getClass().getDeclaredMethod("y");
		assertThrows(InvalidParameterSourceException.class, () -> new MethodParameterSource(null, method));
		assertThrows(InvalidParameterSourceException.class, () -> new MethodParameterSource("", method));
		assertThrows(InvalidParameterSourceException.class,
				() -> new MethodParameterSource(ParameterSource.THIS, method));
	}

	@Test
	void wrongSignature() throws Exception {
		Method moreThan1Parameter = this.getClass().getDeclaredMethod("moreThan1Parameter", AWTEvent.class,
				int.class);
		assertThrows(InvalidParameterSourceException.class,
				() -> new MethodParameterSource("id", moreThan1Parameter));

		Method aVoid = this.getClass().getDeclaredMethod("aVoid");
		assertThrows(InvalidParameterSourceException.class, () -> new MethodParameterSource("id", aVoid));

		Method oneWrongParameter = this.getClass().getDeclaredMethod("oneWrongParameter", String.class);
		assertThrows(InvalidParameterSourceException.class,
				() -> new MethodParameterSource("id", oneWrongParameter));
	}

	@Test
	void zeroParametersOk() throws Exception {
		Method zeroParameters = this.getClass().getDeclaredMethod("zeroParameters");
		MethodParameterSource source = new MethodParameterSource("id", zeroParameters);
		assertEquals(int.class, source.getValueReturnType());
		assertEquals(-5, source.getValue(this, null));
	}

	@Test
	void wrongMethodObjectOwner() throws Exception {
		Method zeroParameters = this.getClass().getDeclaredMethod("zeroParameters");
		MethodParameterSource source = new MethodParameterSource("id", zeroParameters);
		assertThrows(ReflectionException.class, () -> source.getValue(new String(), null));
	}

	@Test
	void oneAwtEventParameterOk() throws Exception {
		Method oneAwtEventParameter = this.getClass().getDeclaredMethod("oneAwtEventParameter",
				ComponentEvent.class);
		MethodParameterSource source = new MethodParameterSource("id", oneAwtEventParameter);
		assertEquals(String.class, source.getValueReturnType());

		//When invoking with null event
		assertEquals("astring", source.getValue(this, null));
		assertNull(injectedAwtEvent);

		//Event type matches parameter sources argument type
		ComponentEvent event = new ComponentEvent(new JButton(), 5);
		assertEquals("astring", source.getValue(this, event));
		assertSame(event, injectedAwtEvent);

		//When cannot be casted, inject null
		ActionEvent actionEvent = new ActionEvent(new JButton(), 0, "cmd");
		assertEquals("astring", source.getValue(this, actionEvent));
		assertNull(injectedAwtEvent);
	}

	@Test
	void equalsAndHashcode() throws Exception {
		Method oneAwtEventParameter = this.getClass().getDeclaredMethod("oneAwtEventParameter",
				ComponentEvent.class);
		MethodParameterSource source1 = new MethodParameterSource("id", oneAwtEventParameter);
		MethodParameterSource source2 = new MethodParameterSource("id", oneAwtEventParameter);
		assertEquals(source1, source2);
		assertEquals(source1.hashCode(), source2.hashCode());
	}

	private int y() {
		return 0;
	}

	private int moreThan1Parameter(AWTEvent event, int y) {
		return 0;
	}

	private int oneWrongParameter(String s) {
		return 0;
	}

	private void aVoid() {
	}

	private String oneAwtEventParameter(ComponentEvent awtEvent) {
		injectedAwtEvent = awtEvent;
		return "astring";
	}

	private int zeroParameters() {
		return -5;
	}
}
