package example;

import io.github.swingboot.control.Control;

public class IncreaseClickCounterControl implements Control<ClickCounterView> {

	@Override
	public void perform(ClickCounterView parameter) {
		parameter.increaseClickCount();
	}

}
