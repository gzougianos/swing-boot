package integration.example;

import io.github.suice.command.InstallCommands;
import io.github.suice.command.annotation.ParameterSource;

@InstallCommands()
public class AdvancedClickCounterView extends ClickCounterView {
	public AdvancedClickCounterView() {
		super();
	}

	@ParameterSource("increase")
	private IncreaseCounterParameter feedIncrease() {
		return new IncreaseCounterParameter(this, 15);
	}
}
