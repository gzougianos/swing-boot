package example;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.github.swingboot.control.annotation.InstallControls;
import io.github.swingboot.control.annotation.KeyBinding;
import io.github.swingboot.control.annotation.OnActionPerformed;
import io.github.swingboot.control.annotation.OnComponentResized;
import io.github.swingboot.control.annotation.ParameterSource;
import io.github.swingboot.control.annotation.multiple.MultipleKeyBinding;

@InstallControls
@OnComponentResized(PrintResizeControl.class)
public class ClickCounterView extends JPanel {
	private static final long serialVersionUID = 4816090097824292469L;

	private int clickCount;
	private JLabel clickCountLabel;

	@OnActionPerformed(value = IncreaseClickCounterControl.class, parameterSource = ParameterSource.THIS)
	//@formatter:off
	@MultipleKeyBinding({ 
			@KeyBinding(keyStroke = "F5", value = IncreaseClickCounterControl.class, parameterSource = ParameterSource.THIS),
			@KeyBinding(keyStroke = "F6", value = IncreaseClickCounterControl.class, parameterSource = ParameterSource.THIS),
		})
	//@formatter:on
	private JButton increaseCounterButton;

	@OnActionPerformed(value = DecreaseClickCounterControl.class, parameterSource = ParameterSource.THIS)
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
