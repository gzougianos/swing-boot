package io.github.swingboot.example.editor.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import io.github.swingboot.concurrency.AssertBackground;

class SaveToTempFileDocumentService implements DocumentService {

	@AssertBackground
	@Override
	public void saveDocument(Document d) throws IOException {
		File file = File.createTempFile("document", ".txt");
		file.deleteOnExit();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Files.write(file.toPath(), d.getContents().getBytes(), StandardOpenOption.CREATE);
	}

}
