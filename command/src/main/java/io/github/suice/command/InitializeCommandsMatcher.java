package io.github.suice.command;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;

public class InitializeCommandsMatcher extends AbstractMatcher<TypeLiteral<?>> {

	@Override
	public boolean matches(TypeLiteral<?> t) {
		return t.getRawType().isAnnotationPresent(InitializeCommands.class);
	}

}
