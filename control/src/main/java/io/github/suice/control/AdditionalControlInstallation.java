package io.github.suice.control;

public interface AdditionalControlInstallation {

	default void beforeAnyControlInstalled(Controls controls) {
	};

	default void afterAllControlsInstalled(Controls controls) {
	};
}
