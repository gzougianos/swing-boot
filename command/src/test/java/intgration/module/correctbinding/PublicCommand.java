package intgration.module.correctbinding;

import io.github.suice.command.Command;

class PublicCommand implements Command<Void> {

	@Override
	public void execute(Void parameter) {

	}

	class InnerNonStaticCommand implements Command<Void> {

		@Override
		public void execute(Void parameter) {
		}

	}

}
