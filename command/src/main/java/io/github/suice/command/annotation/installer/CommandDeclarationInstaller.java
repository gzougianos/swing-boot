package io.github.suice.command.annotation.installer;

import io.github.suice.command.CommandDeclaration;
import io.github.suice.command.ObjectOwnedCommandDeclaration;

public interface CommandDeclarationInstaller {
	boolean supports(CommandDeclaration declaration);

	void install(ObjectOwnedCommandDeclaration declaration);
}
