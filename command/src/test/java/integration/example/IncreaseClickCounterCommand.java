package integration.example;

import javax.inject.Inject;

import io.github.suice.command.Command;

public class IncreaseClickCounterCommand implements Command<Void> {

	private ClickCounterView view;

	@Inject
	public IncreaseClickCounterCommand(ClickCounterView view) {
		this.view = view;
	}

	@Override
	public void execute(Void parameter) {
		view.increaseClickCount();
	}

}
