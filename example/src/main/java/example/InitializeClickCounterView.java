package example;

import java.awt.Color;

import javax.inject.Inject;

import io.github.swingboot.control.Control;

public class InitializeClickCounterView implements Control<ClickCounterView> {

	@Inject
	public InitializeClickCounterView() {
	}

	@Override
	public void perform(ClickCounterView view) {
		view.setBackground(randomColor());
	}

	Color randomColor() {
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		return new Color(r, g, b);
	}
}
