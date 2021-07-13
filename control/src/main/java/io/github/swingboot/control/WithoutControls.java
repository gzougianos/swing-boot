package io.github.swingboot.control;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.EventQueue;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ METHOD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface WithoutControls {

	/**
	 * Removing and re-ardding a listener (that's what {@link WithoutControls} does)
	 * does not guarantee that the event will be fired in-between while the listener
	 * is removed.
	 * 
	 * <br>
	 * <br>
	 * 
	 * This happens for example with ComponentEvents from a JFrame. Consider the
	 * following code:
	 * 
	 * <pre>
	 * ComponentAdapter listener = new ComponentAdapter() {
	 * 	&#64;Override
	 * 	public void componentResized(ComponentEvent e) {
	 * 		System.out.println("RESIZED");
	 * 	}
	 * };
	 * frame.addComponentListener(listener);
	 * 
	 * JButton changeSizeButton = new JButton("Resize Frame");
	 * changeSizeButton.addActionListener(e -&#62; {
	 * 	frame.removeComponentListener(listener);
	 * 	setSize(getSize().width + 15, getSize().height);
	 * 	frame.addComponentListener(listener);
	 * });
	 * 
	 * </pre>
	 * 
	 * Even if the {@code setSize()} is happens while the listener is removed, the
	 * event is still fired. This happens because the event firing from
	 * {@code setSize()} is posted to the {@link EventQueue}. In other words, the
	 * order of execution is:
	 * 
	 * <pre>
	 * frame.removeComponentListener(listener);
	 * frame.addComponentListener(listener);
	 * setSize(getSize().width + 15, getSize().height);
	 * </pre>
	 * 
	 * Setting {@link #waitUntillAllEventsDispatched()} to true solves this "issue".
	 * Installing the listener back to the component will wait until all events are
	 * fired and the {@link EventQueue} is empty. <br>
	 * <br>
	 * See also a related post in <a href=
	 * "https://stackoverflow.com/questions/65854489/is-it-possible-to-avoid-keeping-a-reference-to-the-listeners/65871006">StackOverflow</a>
	 * 
	 * 
	 * @return true whether installing the listeners back to the components should
	 *         be take place when {@link EventQueue} is empty.
	 */
	boolean waitUntillAllEventsDispatched() default false;

}
