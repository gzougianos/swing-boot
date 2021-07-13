package io.github.swingboot.example.editor.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.example.editor.control.HighlightMisspelledWordsControl;

@SuppressWarnings("serial")
@InstallControls
class SpellCheckingOptionsPanel extends JPanel implements SpellCheckingOptionsView {
	// Could have its own control
	@OnActionPerformed(HighlightMisspelledWordsControl.class)
	private JCheckBox spellCheckingCheckBox = new JCheckBox("Enable spell checking");

	@Inject
	public SpellCheckingOptionsPanel() {
		super(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Spell Checking Options View"));

		setPreferredSize(new Dimension(200, 0));
		add(spellCheckingCheckBox, BorderLayout.PAGE_START);
	}

	@Override
	public boolean isSpellCheckingEnabled() {
		return spellCheckingCheckBox.isSelected();
	}
}
