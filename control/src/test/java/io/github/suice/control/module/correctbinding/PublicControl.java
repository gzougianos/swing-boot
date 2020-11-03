package io.github.suice.control.module.correctbinding;

import io.github.suice.control.Control;

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
