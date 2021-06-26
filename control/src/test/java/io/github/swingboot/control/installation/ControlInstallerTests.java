package io.github.swingboot.control.installation;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.swing.JButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.swingboot.control.AdditionalControlInstallation;
import io.github.swingboot.control.Control;
import io.github.swingboot.control.Controls;
import io.github.swingboot.control.InitializedBy;
import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.installation.ControlInstaller.ControlsWereNeverInstalledException;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class ControlInstallerTests {
	private ControlInstaller installer;
	private Controls controls;

	@Test
	void main() {
		EverythingOk obj = new EverythingOk();
		installer.installTo(obj);
		installer.installTo(obj); //try to install again
		obj.button.doClick();

		verify(controls).perform(TestControl.class);
		verifyNoMoreInteractions(controls);
	}

	@Nested
	@UiAll
	class PerformsInitialization {
		@Test
		void main() {
			Target t = new Target();
			installer.installTo(t);
			verify(controls).perform(TestControl.class);
			verifyNoMoreInteractions(controls);
		}

		@InitializedBy(TestControl.class)
		class Target {

		}
	}

	@Nested
	@UiAll
	class InstalledState {
		@Test
		void main() {
			Target t = new Target();
			installer.installTo(t);
			assertTrue(installer.areAllInstalledTo(t));
			assertFalse(installer.areAllUninstalledFrom(t));

			installer.uninstallFrom(t);
			assertTrue(installer.areAllUninstalledFrom(t));
			assertFalse(installer.areAllInstalledTo(t));
		}

		@InstallControls
		class Target {
			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
		}
	}

	@Test
	void reinstallButTheyWereNeverInstalled() {
		assertThrows(ControlsWereNeverInstalledException.class,
				() -> installer.reinstallTo(new EverythingOk()));

		assertThrows(ControlsWereNeverInstalledException.class,
				() -> installer.uninstallFrom(new EverythingOk()));
	}

	@Nested
	@UiAll
	class WithNestedFields {
		@Test
		void main() {
			NestedFieldOnwer nestedFieldOnwer = new NestedFieldOnwer();
			installer.installTo(nestedFieldOnwer);

			nestedFieldOnwer.button.doClick();
			verify(controls, times(1)).perform(TestControl.class);

			nestedFieldOnwer.acceptee.button.doClick();
			verify(controls, times(2)).perform(TestControl.class);
			verifyNoMoreInteractions(controls);
		}

		class NestedFieldOnwer {
			@InstallControls
			private Acceptee acceptee = new Acceptee();

			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
		}

		class Acceptee {
			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
		}
	}

	@Nested
	@UiAll
	class NullNestedField {
		@Test
		void main() {
			NullNestedFieldOnwer nestedFieldOnwer = new NullNestedFieldOnwer();
			assertThrows(NullPointerException.class, () -> installer.installTo(nestedFieldOnwer));
		}

		class NullNestedFieldOnwer {
			@InstallControls
			private Acceptee acceptee;

			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
		}

		class Acceptee {
			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
		}
	}

	@Nested
	@UiAll
	class TwoLevelsNesting {
		@Test
		void main() {
			NestedFieldOnwer nestedFieldOnwer = new NestedFieldOnwer();
			installer.installTo(nestedFieldOnwer);

			nestedFieldOnwer.button.doClick();
			verify(controls, times(1)).perform(TestControl.class);

			nestedFieldOnwer.acceptee.button.doClick();
			verify(controls, times(2)).perform(TestControl.class);

			nestedFieldOnwer.acceptee.deeper.button.doClick();
			verify(controls, times(3)).perform(TestControl.class);
			verifyNoMoreInteractions(controls);
		}

		class NestedFieldOnwer {
			@InstallControls
			private Acceptee acceptee = new Acceptee();

			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
		}

		class Acceptee {
			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
			@InstallControls
			private DeeperAcceptee deeper = new DeeperAcceptee();
		}

		class DeeperAcceptee {
			@OnActionPerformed(TestControl.class)
			private JButton button = new JButton();
		}
	}

	@Test
	void callsAdditionalInstallation() {
		AdditionalControlInstallationImpl obj = new AdditionalControlInstallationImpl();
		installer.installTo(obj);

		assertTrue(obj.beforeCalled);
		assertTrue(obj.afterCalled);
	}

	@Test
	void nullTarget() {
		assertThrows(NullPointerException.class, () -> installer.installTo(null));
	}

	@Test
	void nullComponentToField() {
		NullComponent nullComponent = new NullComponent();
		assertThrows(NullPointerException.class, () -> installer.installTo(nullComponent));
	}

	@BeforeEach
	private void extracted() {
		controls = mock(Controls.class);
		installer = new ControlInstaller(controls);
	}

	@InstallControls
	private static class EverythingOk {

		@OnActionPerformed(TestControl.class)
		private JButton button = new JButton();

	}

	@InstallControls
	private static class AdditionalControlInstallationImpl implements AdditionalControlInstallation {
		private boolean beforeCalled;
		private boolean afterCalled;

		@Override
		public void afterAllControlsInstalled(Controls controls) {
			afterCalled = beforeCalled;
		}

		@Override
		public void beforeAnyControlInstalled(Controls controls) {
			beforeCalled = !afterCalled;
		}
	}

	@InstallControls
	private static class NullComponent {
		@OnActionPerformed(TestControl.class)
		private JButton button;

	}

	private static class TestControl implements Control<Void> {
		@Override
		public void perform(Void parameter) {
		}

	}
}
