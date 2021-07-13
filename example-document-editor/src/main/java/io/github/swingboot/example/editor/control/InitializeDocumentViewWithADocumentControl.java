package io.github.swingboot.example.editor.control;

import javax.inject.Inject;

import io.github.swingboot.control.Control;
import io.github.swingboot.example.editor.model.DocumentFactory;
import io.github.swingboot.example.editor.view.DocumentView;

public class InitializeDocumentViewWithADocumentControl implements Control<Void> {
	private DocumentView view;
	private DocumentFactory factory;

	@Inject
	public InitializeDocumentViewWithADocumentControl(DocumentView view, DocumentFactory factory) {
		this.view = view;
		this.factory = factory;
	}

	@Override
	public void perform(Void parameter) {
		view.setDocument(factory.create());
	}

}
