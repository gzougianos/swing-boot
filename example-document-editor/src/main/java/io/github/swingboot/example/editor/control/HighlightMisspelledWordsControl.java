package io.github.swingboot.example.editor.control;

import java.util.Collection;

import javax.inject.Inject;

import io.github.swingboot.control.Control;
import io.github.swingboot.example.editor.model.MisspelledWord;
import io.github.swingboot.example.editor.model.SpellCheckingService;
import io.github.swingboot.example.editor.view.DocumentView;
import io.github.swingboot.example.editor.view.SpellCheckingOptionsView;

public class HighlightMisspelledWordsControl implements Control<Void> {
	private final SpellCheckingService spellCheckingService;
	private final DocumentView documentView;
	private final SpellCheckingOptionsView spellCheckingOptionsView;

	@Inject
	public HighlightMisspelledWordsControl(SpellCheckingService spellCheckingService, DocumentView documentView,
			SpellCheckingOptionsView spellCheckingOptionsView) {
		this.spellCheckingService = spellCheckingService;
		this.documentView = documentView;
		this.spellCheckingOptionsView = spellCheckingOptionsView;
	}

	@Override
	public void perform(Void parameter) {
		documentView.removeHighlights();

		if (!spellCheckingOptionsView.isSpellCheckingEnabled()) {
			return;
		}

		Collection<MisspelledWord> misspelledWords = spellCheckingService.checkSpelling(documentView.getContents());

		misspelledWords.stream().forEach(word -> {
			documentView.addHighlight(word.getOffset(), word.getLength());
		});
	}

}
