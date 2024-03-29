package example;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.github.swingboot.control.InitializedBy;
import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.ParameterSource;
import io.github.swingboot.control.WithoutControls;
import io.github.swingboot.control.installation.annotation.KeyBinding;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.control.installation.annotation.multiple.MultipleKeyBinding;

@InstallControls
@InitializedBy(value = InitializeClickCounterView.class, parameterSource = ParameterSource.THIS)
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
		setOpaque(true);

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

	@WithoutControls
	public void clickButtonsPassively() {
		increaseCounterButton.doClick();
		increaseCounterButton.doClick();
	}

	public void decreaseClickCount() {
		clickCount--;
		updateLabel();
	}

	private void updateLabel() {
		clickCountLabel.setText("Click count: " + clickCount);
	}

}
