package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnPropertyChange;
import io.github.swingboot.control.installation.annotation.multiple.MultipleOnPropertyChange;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnPropertyChangeInstallationFactoryTets {

	//@formatter:off
	@MultipleOnPropertyChange({ 
			@OnPropertyChange(value = TestControl.class),
			@OnPropertyChange(value = TestControl.class, property = "some_property")
		})
	//@formatter:on
	private JButton button = new JButton();

	private Controls controls = mock(Controls.class);
	private ControlInstaller installer = new ControlInstaller(controls);

	@BeforeEach
	void init() {
		installer.installTo(this);
	}

	@Test
	void bothProperties() {
		button.firePropertyChange("some_property", false, true);

		verify(controls, times(2)).perform(eq(TestControl.class));
	}

	@Test
	void onlyAll() {
		button.firePropertyChange("another_property", false, true);

		verify(controls).perform(eq(TestControl.class));
	}

	@Test
	void uninstall() {
		installer.uninstallFrom(this);
		button.firePropertyChange("another_property", false, true);
		button.firePropertyChange("some_property", false, true);

		verifyNoInteractions(controls);
	}

	private static class TestControl implements Control<Void> {

		@Override
		public void perform(Void parameter) {
		}
	}
}
