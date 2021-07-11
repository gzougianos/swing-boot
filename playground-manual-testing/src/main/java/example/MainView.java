package example;

import java.awt.BorderLayout;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.WithoutControls;
import io.github.swingboot.control.installation.annotation.KeyBinding;

@SuppressWarnings("serial")
@Singleton
@KeyBinding(id = "bind", value = MoveMainViewRightControl.class, keyStroke = "F2", when = KeyBinding.WHEN_IN_FOCUSED_WINDOW)
@InstallControls
public class MainView extends JFrame {

	private ClickCounterView topCounterView;

	@Inject
	public MainView(ClickCounterView topCounterView, ClickCounterView bottomCounterView) {
		super("Example");
		this.topCounterView = topCounterView;
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(topCounterView, BorderLayout.PAGE_START);
		add(bottomCounterView, BorderLayout.PAGE_END);
		pack();
		setLocationByPlatform(true);
	}

	@WithoutControls
	void doWithControlsUninstalled() {
		JComponent contentPane = (JComponent) getContentPane();
		Object input = contentPane.getInputMap().get(KeyStroke.getKeyStroke("F2"));
		System.out.println("F2 Keybinding is installed:" + !(input == null));
		topCounterView.clickButtonsPassively();
	}

}
