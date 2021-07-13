package io.github.swingboot.example.editor.control;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.github.swingboot.example.editor.model.MisspelledWord;
import io.github.swingboot.example.editor.model.SpellCheckingService;
import io.github.swingboot.example.editor.view.DocumentView;
import io.github.swingboot.example.editor.view.SpellCheckingOptionsView;

class HighlightMisspelledWordsControlShould {
	SpellCheckingOptionsView spellCheckingOptionsView = mock(SpellCheckingOptionsView.class);
	SpellCheckingService spellCheckingService = mock(SpellCheckingService.class);
	DocumentView documentView = mock(DocumentView.class);

	@Test
	void just_remove_all_highlights_when_spell_checking_is_disabled() {
		when(spellCheckingOptionsView.isSpellCheckingEnabled()).thenReturn(false);

		perform();

		verify(documentView).removeHighlights();
		verifyNoMoreInteractions(documentView);
	}

	@Test
	void remove_previous_highlights_and_highlight_misspelled_words_when_spell_checking_is_enabled() {
		when(spellCheckingOptionsView.isSpellCheckingEnabled()).thenReturn(true);
		when(documentView.getContents()).thenReturn("misspelledcontents");
		when(spellCheckingService.checkSpelling(eq("misspelledcontents")))
				.thenReturn(Arrays.asList(new MisspelledWord(5, 3)));

		perform();

		verify(documentView).removeHighlights();
		verify(documentView).addHighlight(eq(5), eq(3));
	}

	private void perform() {
		new HighlightMisspelledWordsControl(spellCheckingService, documentView, spellCheckingOptionsView).perform(null);
	}

}
