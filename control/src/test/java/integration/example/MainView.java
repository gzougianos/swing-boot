package integration.example;

import java.awt.BorderLayout;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JFrame;

import io.github.suice.control.InstallControls;
import io.github.suice.control.annotation.listener.KeyBinding;

@SuppressWarnings("serial")
@Singleton
@InstallControls
@KeyBinding(value = MoveMainViewRightControl.class, keyStroke = "F2", when = KeyBinding.WHEN_IN_FOCUSED_WINDOW)
public class MainView extends JFrame {

	@Inject
	public MainView(ClickCounterView topCounterView, ClickCounterView bottomCounterView) {
		super("Example");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(topCounterView, BorderLayout.PAGE_START);
		add(bottomCounterView, BorderLayout.PAGE_END);
		pack();
		setLocationByPlatform(true);
	}
}
