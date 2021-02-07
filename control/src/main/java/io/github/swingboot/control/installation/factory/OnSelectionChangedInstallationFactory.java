package io.github.swingboot.control.installation.factory;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import io.github.swingboot.control.installation.annotation.OnSelectionChanged;

public class OnSelectionChangedInstallationFactory implements ControlInstallationFactory {
	OnSelectionChangedInstallationFactory() {
	}

	@Override
	public ControlInstallation create(InstallationContext context) {
		OnSelectionChanged onSelectionChanged = context.getAnnotationAs(OnSelectionChanged.class);
		final Object target = context.getTarget();
		final ListSelectionModel selectionModel = getTargetSelectionModel(target);

		ListSelectionListener listener = event -> {
			if (onSelectionChanged.valueIsAdjusting().matches(event.getValueIsAdjusting())) {
				context.getEventConsumer().accept(event);
			}
		};

		return new ControlInstallation(() -> {
			selectionModel.addListSelectionListener(listener);
		}, () -> {
			selectionModel.removeListSelectionListener(listener);
		});
	}

	private ListSelectionModel getTargetSelectionModel(final Object target) {
		if (target instanceof JList<?>)
			return ((JList<?>) target).getSelectionModel();

		if (target instanceof JTable)
			return ((JTable) target).getSelectionModel();

		if (target instanceof ListSelectionModel)
			return (ListSelectionModel) target;

		throw new UnsupportedOperationException(
				"@OnSelectionChanged cannot be installed to target of type " + target.getClass());
	}
}