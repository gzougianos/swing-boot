package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.EventObject;
import java.util.function.Consumer;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.installation.annotation.BooleanValue;
import io.github.swingboot.control.installation.annotation.OnSelectionChanged;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@SuppressWarnings("unchecked")
@UiAll
class OnSelectionChangedInstallationFactoryTests {
	@OnSelectionChanged(value = TestControl.class, valueIsAdjusting = BooleanValue.TRUE)
	private int field;

	@Test
	void test() throws Exception {
		JList<String> list = new JList<>(new String[] { "hello", "world" });
		Consumer<EventObject> consumer = mock(Consumer.class);
		OnSelectionChanged annotation = getClass().getDeclaredField("field")
				.getAnnotation(OnSelectionChanged.class);

		ControlInstallation installation = new OnSelectionChangedInstallationFactory()
				.createInstallation(new InstallationContext(this, list, annotation, consumer));

		installation.install();
		list.setSelectedValue("world", false);
		verifyNoInteractions(consumer);

		list.setValueIsAdjusting(true);
		list.setSelectedValue("hello", false);
		verify(consumer).accept(isA(ListSelectionEvent.class));

		reset(consumer);

		installation.uninstall();
		list.setSelectedValue("world", false);
		list.setSelectedValue("hello", false);
		verifyNoInteractions(consumer);
	}

	private static class TestControl implements Control<Integer> {
		@Override
		public void perform(Integer parameter) {
		}
	}
}
