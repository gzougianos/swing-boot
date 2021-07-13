package io.github.swingboot.example.editor.view;

import java.awt.BorderLayout;

import javax.inject.Inject;
import javax.swing.JFrame;

import io.github.swingboot.control.InstallControls;

@SuppressWarnings("serial")
@InstallControls
public class MainView extends JFrame {

	@Inject
	public MainView(DocumentPanel documentView, SpellCheckingOptionsPanel spellCheckingOptionsView) {
		super("Document Editor Application");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		add(documentView, BorderLayout.CENTER);
		add(spellCheckingOptionsView, BorderLayout.LINE_END);

		pack();
		setLocationByPlatform(true);
	}
}
