package io.github.swingboot.control.event;

import java.util.EventObject;

import javax.swing.text.Document;
import javax.swing.text.Element;

public class DocumentEvent extends EventObject implements javax.swing.event.DocumentEvent {
	private static final long serialVersionUID = -4764637620064158863L;
	private final javax.swing.event.DocumentEvent actual;

	public DocumentEvent(javax.swing.event.DocumentEvent actual) {
		super(actual.getDocument());
		this.actual = actual;
	}

	public javax.swing.event.DocumentEvent getActual() {
		return actual;
	}

	@Override
	public int getOffset() {
		return actual.getOffset();
	}

	@Override
	public int getLength() {
		return actual.getLength();
	}

	@Override
	public Document getDocument() {
		return actual.getDocument();
	}

	@Override
	public EventType getType() {
		return actual.getType();
	}

	@Override
	public ElementChange getChange(Element elem) {
		return actual.getChange(elem);
	}

}
