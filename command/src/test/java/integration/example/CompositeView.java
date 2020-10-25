package integration.example;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.google.inject.Inject;

public class CompositeView extends JPanel {

	@Inject
	private CompositeView(ClickCounterView counterView) {
		super(new BorderLayout());
		setBorder(new TitledBorder("Composite"));
		add(counterView);
	}
}
