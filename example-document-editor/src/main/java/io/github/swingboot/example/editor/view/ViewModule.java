package io.github.swingboot.example.editor.view;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ViewModule extends AbstractModule {

	@Override
	protected void configure() {
		// Stateful objects
		bind(DocumentPanel.class).in(Singleton.class);
		bind(DocumentView.class).to(DocumentPanel.class).in(Singleton.class);

		bind(MainView.class).in(Singleton.class);

		bind(SpellCheckingOptionsPanel.class).in(Singleton.class);
		bind(SpellCheckingOptionsView.class).to(SpellCheckingOptionsPanel.class).in(Singleton.class);
	}
}
