package io.github.suice.command;

public interface Command<T> {

	void execute(T parameter);
}
