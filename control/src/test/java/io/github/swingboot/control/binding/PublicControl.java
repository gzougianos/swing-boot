package io.github.swingboot.control.binding;

import io.github.swingboot.control.Control;

class PublicControl implements Control<Void> {

	@Override
	public void perform(Void parameter) {

	}

	class InnerNonStaticControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}

	}

}
