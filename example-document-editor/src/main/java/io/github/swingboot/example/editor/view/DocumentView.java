package io.github.swingboot.example.editor.view;

import io.github.swingboot.example.editor.model.Document;

public interface DocumentView {
	void setDocument(Document d);

	Document getDocument();

	String getContents();

	void setContents(String contents);

	void setDocumentIsSaving(boolean b);

	void showDocumentSaved();

	void showDocumentWasNotSaved();

	void removeHighlights();

	void addHighlight(int index, int length);
}
