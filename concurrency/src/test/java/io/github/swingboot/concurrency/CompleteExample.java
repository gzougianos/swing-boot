package io.github.swingboot.concurrency;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CompleteExample {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Injector injector = Guice.createInjector(new ConcurrencyModule());
			injector.getInstance(MainView.class).setVisible(true);
		});
	}

	@SuppressWarnings("serial")
	static class MainView extends JFrame implements ActionListener {
		private JButton button;
		private JTextArea textArea;

		public MainView() {
			super("example");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			setLayout(new BorderLayout());

			button = new JButton("Do in background");
			button.addActionListener(this);

			textArea = new JTextArea(20, 40);

			add(button, BorderLayout.PAGE_START);
			add(new JScrollPane(textArea), BorderLayout.CENTER);

			pack();
			setLocationByPlatform(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < 20; i++) {
				doHeavyTask();
			}

			System.out.println("Started background task.");
		}

		@InBackground
		void doHeavyTask() {
			assertFalse(SwingUtilities.isEventDispatchThread());
			disableButton();
			try {
				Thread.sleep((long) (Math.random() * 1000));
				publish(Thread.currentThread().getName() + " is publishing");
				Thread.sleep((long) (Math.random() * 1000));

				enableButton();
				Thread.sleep((long) (Math.random() * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@InUi
		String enableButton() {
			assertTrue(SwingUtilities.isEventDispatchThread());
			button.setEnabled(true);
			button.setText("Do in background");
			return "done";
		}

		@InUi
		void disableButton() {
			assertTrue(SwingUtilities.isEventDispatchThread());
			button.setEnabled(false);
			button.setText("Doing in background...");
		}

		@InUi
		void publish(String s) {
			assertTrue(SwingUtilities.isEventDispatchThread());
			textArea.append(s + System.lineSeparator());
		}

	}
}
