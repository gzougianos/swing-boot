package io.github.swingboot.example.editor.model;

import java.io.IOException;

public interface DocumentService {

	void saveDocument(Document d) throws IOException;
}
