package io.github.swingboot.example.editor.control;

import javax.inject.Inject;

import io.github.swingboot.control.Control;
import io.github.swingboot.example.editor.model.SpellCheckingService;
import io.github.swingboot.example.editor.view.DocumentView;

public class CorrectSpellingControl implements Control<Void> {
	private final DocumentView documentView;
	private final SpellCheckingService spellCheckingService;

	@Inject
	public CorrectSpellingControl(DocumentView documentView, SpellCheckingService spellCheckingService) {
		this.documentView = documentView;
		this.spellCheckingService = spellCheckingService;
	}

	@Override
	public void perform(Void parameter) {
		String contents = documentView.getContents();
		String correctContents = spellCheckingService.correctSpelling(contents);
		documentView.setContents(correctContents);
	}

}
