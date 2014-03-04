package org.pepstock.jem.gwt.client.panels.resources.inspector.custom;

/**
 * Interface to be implemented by objects that should handle {@link ListEditor} events
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <T> the value type
 */
public interface ListEditorListener<T> {
	
	/**
	 * Triggered when values are changed.
	 * @param newValues an array containing all the values
	 */
	void valuesChanged(T newValues);
	
}
