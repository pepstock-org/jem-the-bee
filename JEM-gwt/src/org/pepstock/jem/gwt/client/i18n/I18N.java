package org.pepstock.jem.gwt.client.i18n;

import com.google.gwt.i18n.client.Constants;

/**
 * @see Constants
 * @author Marco "Fuzzo" Cuccato
 */
public interface I18N extends Constants {

	  /**
	   * Returns the localized decimal separator.
	 * @return 
	   */
	  String decimalSeparator();

	  /**
	   * Returns the localized thousands separator.
	 * @return 
	   */
	  String thousandsSeparator();
	
}
