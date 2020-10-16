package intgration.module.correctbinding;

import java.util.Optional;

import io.github.suice.command.Command;

public abstract class AbstractCommand implements Command<Void> {

	@Override
	public void execute(Optional<Void> parameter) {
	}

}
