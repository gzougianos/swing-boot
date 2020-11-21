package integration.example;

import javax.annotation.Nullable;

import io.github.suice.control.Control;

public class IncreaseClickCounterControl implements Control<ClickCounterView> {

	@Override
	public void perform(@Nullable ClickCounterView parameter) {
		if (parameter != null)
			parameter.increaseClickCount();
	}

}
