package integration.example;

import io.github.suice.command.Command;

public class IncreaseClickCounterCommand implements Command<IncreaseCounterParameter> {

	@Override
	public void execute(IncreaseCounterParameter parameter) {
		for (int i = 0; i < parameter.getCount(); i++) {
			parameter.getClickCounterView().increaseClickCount();
		}

	}

}
