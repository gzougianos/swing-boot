package integration.example;

import java.awt.event.ComponentEvent;

import io.github.suice.command.Command;

public class PrintResizeCommand implements Command<ComponentEvent> {

	@Override
	public void execute(ComponentEvent parameter) {
		System.out.println(parameter);
	}

}
