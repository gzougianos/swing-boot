package io.github.suice.command.annotation.installer.creator;

import java.lang.annotation.Annotation;
import java.util.EventListener;

public interface ListenerCreator<A extends Annotation, L extends EventListener> {

	L createListener(A annotation);
}
