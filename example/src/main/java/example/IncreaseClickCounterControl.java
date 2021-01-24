package example;

import javax.annotation.Nullable;

import io.github.swingboot.control.Control;

public class IncreaseClickCounterControl implements Control<ClickCounterView> {

	@Override
	public void perform(@Nullable ClickCounterView parameter) {
		if (parameter != null)
			parameter.increaseClickCount();
	}

}
