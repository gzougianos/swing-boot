package io.github.swingboot.example.editor.control;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import io.github.swingboot.example.editor.model.SpellCheckingService;
import io.github.swingboot.example.editor.view.DocumentView;

class CorrectSpellingControlShould {

	@Test
	void should_fix_misspells_on_the_editor() {
		DocumentView view = mock(DocumentView.class);
		when(view.getContents()).thenReturn("misspelled_contents");

		SpellCheckingService service = mock(SpellCheckingService.class);
		when(service.correctSpelling(eq("misspelled_contents"))).thenReturn("correct contents");

		new CorrectSpellingControl(view, service).perform(null);

		verify(view).setContents(eq("correct contents"));
	}

}
