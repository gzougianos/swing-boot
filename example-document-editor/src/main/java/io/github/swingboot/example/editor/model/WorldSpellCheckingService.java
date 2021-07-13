package io.github.swingboot.example.editor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class WorldSpellCheckingService implements SpellCheckingService {

	@Override
	public Collection<MisspelledWord> checkSpelling(String text) {
		List<MisspelledWord> result = new ArrayList<>();
		for (int index = text.indexOf("wrold"); index >= 0; index = text.indexOf("wrold", index + 1)) {
			result.add(new MisspelledWord(index, "wrold".length()));
		}
		return result;
	}

	@Override
	public String correctSpelling(String text) {
		return text.replaceAll("wrold", "world");
	}

}
