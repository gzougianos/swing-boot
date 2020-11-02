package io.github.suice.command.annotation.installer.creator;

import java.awt.AWTEvent;
import java.lang.annotation.Annotation;
import java.util.EventListener;
import java.util.function.Consumer;

public interface ListenerCreator<A extends Annotation, L extends EventListener> {

	L createListener(A annotation, Consumer<AWTEvent> eventConsumer);

}
