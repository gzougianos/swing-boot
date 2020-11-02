package integration.example;

import io.github.suice.command.Command;

public class IncreaseClickCounterCommand implements Command<ClickCounterView> {

	@Override
	public void execute(ClickCounterView parameter) {
		parameter.increaseClickCount();
	}

}
