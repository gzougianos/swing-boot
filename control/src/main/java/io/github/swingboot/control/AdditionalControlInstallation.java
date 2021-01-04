package io.github.swingboot.control;

public interface AdditionalControlInstallation {

	default void beforeAnyControlInstalled(Controls controls) {
	};

	default void afterAllControlsInstalled(Controls controls) {
	};
}
