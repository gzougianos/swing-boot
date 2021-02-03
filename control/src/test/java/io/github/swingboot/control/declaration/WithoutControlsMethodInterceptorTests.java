package io.github.swingboot.control.declaration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Singleton;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.WithoutControls;
import io.github.swingboot.control.binding.ControlModule;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.testutils.UiAll;
import io.github.swingboot.testutils.UiExtension;

@ExtendWith(UiExtension.class)
@UiAll
class WithoutControlsMethodInterceptorTests {

	@Test
	void passiveViewOnMethod() throws Throwable {
		Injector injector = Guice.createInjector(new ControlModule(ButtonControl.class));
		ButtonControl buttonControl = injector.getInstance(ButtonControl.class);
		ButtonControl2 buttonControl2 = injector.getInstance(ButtonControl2.class);
		OnMethod view = injector.getInstance(OnMethod.class);

		view.activeClicks();

		assertEquals(1, buttonControl.timesFired);
		assertEquals(1, buttonControl2.timesFired);

		view.passiveClicks();

		assertEquals(1, buttonControl.timesFired);
		assertEquals(1, buttonControl2.timesFired);

		view.activeClicks();
		assertEquals(2, buttonControl.timesFired);
		assertEquals(2, buttonControl2.timesFired);
	}

	@Test
	void exceptionInInvocation() {
		Injector injector = Guice.createInjector(new ControlModule(ButtonControl.class));
		ButtonControl buttonControl = injector.getInstance(ButtonControl.class);
		ButtonControl2 buttonControl2 = injector.getInstance(ButtonControl2.class);
		OnMethod view = injector.getInstance(OnMethod.class);

		view.activeClicks();

		assertEquals(1, buttonControl.timesFired);
		assertEquals(1, buttonControl2.timesFired);

		view.throwException = true;
		try {
			view.passiveClicks();
		} catch (RuntimeException e) {

		}

		assertEquals(1, buttonControl.timesFired);
		assertEquals(1, buttonControl2.timesFired);

		view.activeClicks();
		assertEquals(2, buttonControl.timesFired);
		assertEquals(2, buttonControl2.timesFired);
	}

	@InstallControls
	@Singleton
	static class OnMethod {
		@OnActionPerformed(ButtonControl.class)
		private JButton button = new JButton();

		@OnActionPerformed(ButtonControl2.class)
		private JButton button2 = new JButton();
		boolean throwException = false;

		@WithoutControls
		void passiveClicks() {
			if (throwException)
				throw new RuntimeException();
			button.doClick();
			button2.doClick();
		}

		void activeClicks() {
			button.doClick();
			button2.doClick();
		}
	}

	@Test
	void onNestedCallsReinstallOnlyOneTimeAtTheEnd() {
		Injector injector = Guice.createInjector(new ControlModule(ButtonControl.class));
		ButtonControl buttonControl = injector.getInstance(ButtonControl.class);
		ButtonControl2 buttonControl2 = injector.getInstance(ButtonControl2.class);
		WithNestedCall view = injector.getInstance(WithNestedCall.class);

		view.passiveClicks();
		assertEquals(0, buttonControl.timesFired);
		assertEquals(0, buttonControl2.timesFired);

		view.activeClick();

		assertEquals(2, buttonControl.timesFired);
		assertEquals(0, buttonControl2.timesFired);
	}

	@InstallControls
	@Singleton
	static class WithNestedCall {
		@OnActionPerformed(ButtonControl.class)
		private JButton button = new JButton();

		@OnActionPerformed(ButtonControl2.class)
		private JButton button2 = new JButton();

		@WithoutControls
		void passiveClicks() {
			button.doClick();
			nestedCall();
			button.doClick();
		}

		void activeClick() {
			button.doClick();
			nestedCall();
			button.doClick();
		}

		@WithoutControls
		void nestedCall() {
			button2.doClick();
		}
	}

	@Singleton
	static class ButtonControl implements Control<Void> {
		int timesFired = 0;

		public ButtonControl() {
		}

		@Override
		public void perform(Void parameter) {
			timesFired++;
		}
	}

	@Singleton
	static class ButtonControl2 implements Control<Void> {
		int timesFired = 0;

		public ButtonControl2() {
		}

		@Override
		public void perform(Void parameter) {
			timesFired++;
		}
	}
}
