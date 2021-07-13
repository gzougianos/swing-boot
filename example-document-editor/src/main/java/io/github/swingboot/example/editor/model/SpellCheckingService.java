package io.github.swingboot.example.editor.model;

import java.util.Collection;

public interface SpellCheckingService {

	Collection<MisspelledWord> checkSpelling(String text);

	String correctSpelling(String text);
}
