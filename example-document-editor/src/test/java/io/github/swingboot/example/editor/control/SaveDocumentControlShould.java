package io.github.swingboot.example.editor.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import io.github.swingboot.example.editor.model.Document;
import io.github.swingboot.example.editor.model.DocumentService;
import io.github.swingboot.example.editor.view.DocumentView;

class SaveDocumentControlShould {
	private DocumentService documentService = mock(DocumentService.class);
	private DocumentView documentView = mock(DocumentView.class);
	private Document document = new Document();

	@Test
	void set_contents_of_document_view() {
		when(documentView.getContents()).thenReturn("super_contents");
		perform();

		assertEquals("super_contents", document.getContents());
	}

	@Test
	void tell_service_to_store_document() throws Exception {
		perform();
		verify(documentService).saveDocument(eq(document));
	}

	@Test
	void show_the_user_that_saving_is_loading() {
		InOrder order = inOrder(documentView);
		perform();

		order.verify(documentView).setDocumentIsSaving(eq(true));
		order.verify(documentView).setDocumentIsSaving(eq(false));
	}

	@Test
	void show_the_user_that_document_is_saved() {
		perform();
		verify(documentView).showDocumentSaved();
	}

	@Test
	void show_the_user_that_document_was_not_saved_when_service_fails() throws Exception {
		doThrow(IOException.class).when(documentService).saveDocument(eq(document));
		perform();
		verify(documentView).showDocumentWasNotSaved();
	}

	@BeforeEach
	void initViewWithDocument() {
		when(documentView.getDocument()).thenReturn(document);
	}

	private void perform() {
		new SaveDocumentControl(documentService, documentView).perform(null);
	}

}
