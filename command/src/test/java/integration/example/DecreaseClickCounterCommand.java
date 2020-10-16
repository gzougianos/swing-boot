package integration.example;

import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.inject.Inject;

import io.github.suice.command.Command;

public class DecreaseClickCounterCommand implements Command<ActionEvent> {

	private ClickCounterView view;

	@Inject
	public DecreaseClickCounterCommand(ClickCounterView view) {
		this.view = view;
	}

	@Override
	public void execute(Optional<ActionEvent> parameter) {
		System.out.println("Event: " + parameter.get());
		view.decreaseClickCount();
	}

}
