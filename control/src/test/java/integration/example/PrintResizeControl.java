package integration.example;

import java.awt.event.ComponentEvent;

import io.github.suice.control.Control;

public class PrintResizeControl implements Control<ComponentEvent> {

	@Override
	public void perform(ComponentEvent parameter) {
		System.out.println(parameter);
	}

}
