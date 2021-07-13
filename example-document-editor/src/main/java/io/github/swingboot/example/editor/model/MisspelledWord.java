package io.github.swingboot.example.editor.model;

import java.util.Objects;

public class MisspelledWord {
	private int offset;
	private int length;

	public MisspelledWord(int offset, int length) {
		super();
		this.offset = offset;
		this.length = length;
	}

	public final int getOffset() {
		return offset;
	}

	public final int getLength() {
		return length;
	}

	@Override
	public int hashCode() {
		return Objects.hash(length, offset);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MisspelledWord other = (MisspelledWord) obj;
		return length == other.length && offset == other.offset;
	}

}
