package integration.example;

import io.github.suice.control.Control;

public class IncreaseClickCounterControl implements Control<ClickCounterView> {

	@Override
	public void perform(ClickCounterView parameter) {
		parameter.increaseClickCount();
	}

}
