package integration.example;

import io.github.suice.control.Control;

public class DecreaseClickCounterControl implements Control<ClickCounterView> {

	@Override
	public void perform(ClickCounterView view) {
		view.decreaseClickCount();
	}

}
