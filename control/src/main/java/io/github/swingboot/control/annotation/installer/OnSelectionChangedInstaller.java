package io.github.swingboot.control.annotation.installer;

import java.lang.annotation.Annotation;
import java.util.EventObject;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import io.github.swingboot.control.annotation.OnSelectionChanged;

public class OnSelectionChangedInstaller implements AnnotationInstaller {

	@Override
	public ControlInstallation createInstallation(Annotation annotation, Object target,
			Consumer<EventObject> eventConsumer) {
		OnSelectionChanged onSelectionChanged = (OnSelectionChanged) annotation;
		ListSelectionModel selectionModel;
		if (target instanceof JList<?>)
			selectionModel = ((JList<?>) target).getSelectionModel();
		else if (target instanceof JTable)
			selectionModel = ((JTable) target).getSelectionModel();
		else if (target instanceof ListSelectionModel)
			selectionModel = (ListSelectionModel) target;
		else
			throw new UnsupportedOperationException(
					"@OnSelectionChanged cannot be installed to target of type " + target.getClass());

		Predicate<ListSelectionEvent> predicate = event -> onSelectionChanged.valueIsAdjusting()
				.matches(event.getValueIsAdjusting());

		Listener listener = new Listener(eventConsumer, predicate);
		return new ControlInstallation(() -> {
			selectionModel.addListSelectionListener(listener);
		}, () -> {
			selectionModel.removeListSelectionListener(listener);
		});
	}

	private static class Listener implements ListSelectionListener {
		private Consumer<EventObject> eventConsumer;
		private Predicate<ListSelectionEvent> predicate;

		public Listener(Consumer<EventObject> eventConsumer, Predicate<ListSelectionEvent> predicate) {
			this.eventConsumer = eventConsumer;
			this.predicate = predicate;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (predicate.test(e))
				eventConsumer.accept(e);
		}
	}
}
