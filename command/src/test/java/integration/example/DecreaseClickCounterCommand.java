package integration.example;

import java.util.Optional;

import javax.inject.Inject;

import io.github.suice.command.Command;

public class DecreaseClickCounterCommand implements Command<Void> {

	private ClickCounterView view;

	@Inject
	public DecreaseClickCounterCommand(ClickCounterView view) {
		this.view = view;
	}

	@Override
	public void execute(Optional<Void> parameter) {
		view.decreaseClickCount();
	}

}
