package io.github.swingboot.example.editor.model;

import com.google.inject.AbstractModule;

public class ModelModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(SpellCheckingService.class).to(WorldSpellCheckingService.class);
		bind(DocumentService.class).to(SaveToTempFileDocumentService.class);
		bind(DocumentFactory.class);
	}
}
