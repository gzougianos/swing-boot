package intgration.module.correctbinding;

import java.util.Optional;

import io.github.suice.command.Command;

class PublicCommand implements Command<Void> {

	@Override
	public void execute(Optional<Void> parameter) {

	}

	class InnerNonStaticCommand implements Command<Void> {

		@Override
		public void execute(Optional<Void> parameter) {
		}

	}

}
