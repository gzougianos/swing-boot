package io.github.swingboot.example.editor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import io.github.swingboot.control.InitializedBy;
import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.WithoutControls;
import io.github.swingboot.control.installation.annotation.KeyBinding;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.control.installation.annotation.OnDocumentUpdate;
import io.github.swingboot.example.editor.control.CorrectSpellingControl;
import io.github.swingboot.example.editor.control.HighlightMisspelledWordsControl;
import io.github.swingboot.example.editor.control.InitializeDocumentViewWithADocumentControl;
import io.github.swingboot.example.editor.control.SaveDocumentControl;
import io.github.swingboot.example.editor.model.Document;

@SuppressWarnings("serial")
@InstallControls
@InitializedBy(InitializeDocumentViewWithADocumentControl.class)
@KeyBinding(value = SaveDocumentControl.class, keyStroke = "control S", when = KeyBinding.WHEN_IN_FOCUSED_WINDOW)
class DocumentPanel extends JPanel implements DocumentView {
	@OnDocumentUpdate(HighlightMisspelledWordsControl.class)
	private JTextArea contentsArea = new JTextArea(25, 40);

	@OnActionPerformed(SaveDocumentControl.class)
	private JButton saveDocumentButton = new JButton("Save Document");

	@OnActionPerformed(CorrectSpellingControl.class)
	private JButton correctSpellingButton = new JButton("Correct Spelling");
	private Document document;

	@Inject
	public DocumentPanel() {
		super(new BorderLayout());
		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(saveDocumentButton);
		topPanel.add(correctSpellingButton);
		add(topPanel, BorderLayout.PAGE_START);

		setBorder(BorderFactory.createTitledBorder("Document View"));
		add(new JScrollPane(contentsArea), BorderLayout.CENTER);

		// Windows look and feel is on drugs
		contentsArea.setFont(saveDocumentButton.getFont());
	}

	@Override
	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@WithoutControls
	public void setContentsWithoutSpellChecking(String contents) {
		contentsArea.setText(contents);
	}

	@Override
	public void setDocumentIsSaving(boolean b) {
		if (b)
			saveDocumentButton.setText("Saving...");
		else
			saveDocumentButton.setText("Save document");
		saveDocumentButton.setEnabled(!b);
	}

	@Override
	public String getContents() {
		return contentsArea.getText();
	}

	@Override
	@WithoutControls
	public void setContents(String contents) {
		contentsArea.setText(contents);
	}

	@Override
	public void showDocumentSaved() {
		JOptionPane.showMessageDialog(this, "Document was saved");
	}

	@Override
	public void showDocumentWasNotSaved() {
		JOptionPane.showMessageDialog(this, "Document was not saved");
	}

	@Override
	public void removeHighlights() {
		contentsArea.getHighlighter().removeAllHighlights();
	}

	@Override
	public void addHighlight(int index, int length) {
		try {
			contentsArea.getHighlighter().addHighlight(index, index + length,
					new DefaultHighlighter.DefaultHighlightPainter(Color.red));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
