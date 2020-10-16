package integration.example;

import java.awt.FlowLayout;

import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.github.suice.command.InstallCommands;
import io.github.suice.command.annotation.OnActionPerformed;

@InstallCommands
@Singleton
public class ClickCounterView extends JPanel {
	private static final long serialVersionUID = 4816090097824292469L;
	private int clickCount;
	private JLabel clickCountLabel;

	@OnActionPerformed(IncreaseClickCounterCommand.class)
	private JButton increaseCounterButton;

	@OnActionPerformed(DecreaseClickCounterCommand.class)
	private JButton decreaseCounterButton;

	public ClickCounterView() {
		super(new FlowLayout());

		increaseCounterButton = new JButton("Increase");
		decreaseCounterButton = new JButton("Decrease");

		clickCountLabel = new JLabel("Click count: 0");

		add(clickCountLabel);
		add(increaseCounterButton);
		add(decreaseCounterButton);
	}

	public void increaseClickCount() {
		clickCount++;
		updateLabel();
	}

	public void decreaseClickCount() {
		clickCount--;
		updateLabel();
	}

	private void updateLabel() {
		clickCountLabel.setText("Click count: " + clickCount);
	}

}
