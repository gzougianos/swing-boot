package io.github.suice.command;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;

class InstallCommandsMatcher extends AbstractMatcher<TypeLiteral<?>> {

	@Override
	public boolean matches(TypeLiteral<?> t) {
		return t.getRawType().isAnnotationPresent(InstallCommands.class);
	}

}
