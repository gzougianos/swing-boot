package io.github.suice.command;

import java.util.Optional;

public interface Command<T> {

	void execute(Optional<T> parameter);
}
