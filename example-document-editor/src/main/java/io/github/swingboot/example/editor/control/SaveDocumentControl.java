package io.github.swingboot.example.editor.control;

import java.io.IOException;

import javax.inject.Inject;

import io.github.swingboot.concurrency.InBackground;
import io.github.swingboot.concurrency.InUi;
import io.github.swingboot.control.Control;
import io.github.swingboot.example.editor.model.Document;
import io.github.swingboot.example.editor.model.DocumentService;
import io.github.swingboot.example.editor.view.DocumentView;

public class SaveDocumentControl implements Control<Void> {

	private final DocumentService documentService;
	private final DocumentView view;

	@Inject
	public SaveDocumentControl(DocumentService documentService, DocumentView view) {
		this.documentService = documentService;
		this.view = view;
	}

	@Override
	public void perform(Void parameter) {
		Document doc = view.getDocument();

		doc.setContents(view.getContents());

		view.setDocumentIsSaving(true);

		saveDocumentInBackground(doc);
	}

	@InBackground
	void saveDocumentInBackground(Document doc) {
		try {
			documentService.saveDocument(doc);
			showDocumentSaved();
		} catch (IOException e) {
			e.printStackTrace();
			showDocumentNotSaved();
		}
	}

	@InUi
	void showDocumentSaved() {
		view.showDocumentSaved();
		view.setDocumentIsSaving(false);
	}

	@InUi
	void showDocumentNotSaved() {
		view.showDocumentWasNotSaved();
		view.setDocumentIsSaving(false);
	}

}
