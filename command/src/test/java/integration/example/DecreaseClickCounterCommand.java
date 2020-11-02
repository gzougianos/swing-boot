package integration.example;

import io.github.suice.command.Command;

public class DecreaseClickCounterCommand implements Command<ClickCounterView> {

	@Override
	public void execute(ClickCounterView view) {
		view.decreaseClickCount();
	}

}
