package io.github.swingboot.control.installation.factory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.ParameterSource;
import io.github.swingboot.control.event.DocumentEvent;
import io.github.swingboot.control.installation.ControlInstaller;
import io.github.swingboot.control.installation.annotation.OnDocumentUpdate;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class OnDocumentUpdateInstallationFactoryTests {

	@OnDocumentUpdate(value = TestControl.class, parameterSource = "parsource")
	private JTextField field = new JTextField();

	@Test
	void test() throws BadLocationException {
		Controls mock = mock(Controls.class);
		ControlInstaller installer = new ControlInstaller(mock);
		installer.installControls(this);

		field.setText("helo");
		verify(mock).perform(eq(TestControl.class), eq(0));

		field.getDocument().remove(3, 1); //text is now 'hel'

		verify(mock).perform(eq(TestControl.class), eq(3));

		installer.uninstallFrom(this);

		field.setText("heloThere");
		verifyNoMoreInteractions(mock);
	}

	//Parameter source test for custom document event
	@ParameterSource("parsource")
	private Integer parSource(DocumentEvent ev) {
		return ev.getOffset();
	}

	private static class TestControl implements Control<Integer> {

		@Override
		public void perform(Integer parameter) {
		}
	}
}
