package integration.example;

public class IncreaseCounterParameter {
	private ClickCounterView clickCounterView;
	private int count;

	public IncreaseCounterParameter(ClickCounterView clickCounterView, int count) {
		super();
		this.clickCounterView = clickCounterView;
		this.count = count;
	}

	public ClickCounterView getClickCounterView() {
		return clickCounterView;
	}

	public int getCount() {
		return count;
	}

}
