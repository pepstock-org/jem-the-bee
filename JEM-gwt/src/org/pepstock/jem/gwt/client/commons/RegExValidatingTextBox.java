/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.gwt.client.commons;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is an extension of {@link TextBox} useful for 
 * fields that have to be validated in the forms.
 * It uses a regular expression for validation.
 * If the text is not valid, turns red the borders.
 * 
 * @see #validatingRegularExpression
 * @see TextBox
 * @author Alessandro Zambrini
 *
 */
public final class RegExValidatingTextBox extends TextBox{

	/**
	 * The regular expression for validation.
	 */
	private String validatingRegularExpression = null;

	/**
	 * Title if paste is disabled
	 */
	private static final String PASTE_DISABLED_MESSAGE = "Paste disabled here!";
	
	/**
	 * Default value for {@link isPasteEnabled}
	 */
	private static final boolean DEFAULT_IS_PASTE_ENABLED = true;

	/**
	 * Fields containing if paste is enabled. <br>
	 * Default value is {@link #DEFAULT_IS_PASTE_ENABLED}.
	 * 
	 */
	private boolean isPasteEnabled = DEFAULT_IS_PASTE_ENABLED;
	
	/**
	 * Title in case text is not valid. Not mandatory.
	 */
	private String errorTitle;
	
	/**
	 * Title in case text is valid. Not mandatory.
	 */
	private String okTitle;
		
	static{
		Styles.INSTANCE.textBox().ensureInjected();
	}
	

	/**
	 * Constructor that sets the {@link #errorTitle} to the parameter <code>errorTitle</code> and
	 * the {@link #validatingRegularExpression} to the parameter <code>validatingRegularExpression</code>.
	 * Sets the property to enable or disable the paste.
	 * Turns red the border.
	 * 
	 * @param validatingRegularExpression the regular expression to be used for validation.
	 * @param errorTitle the title in the case the text is not valid.
	 * @param isPasteEnabled property to enable or disable the paste.
	 */
	public RegExValidatingTextBox(String validatingRegularExpression, String errorTitle, boolean isPasteEnabled){
		super();
		this.errorTitle = errorTitle;
		this.validatingRegularExpression = validatingRegularExpression;
		this.isPasteEnabled = isPasteEnabled;
		if (!isPasteEnabled) {
			super.sinkEvents(Event.ONPASTE);
			this.errorTitle = this.errorTitle + " " + PASTE_DISABLED_MESSAGE;
			this.okTitle = PASTE_DISABLED_MESSAGE;
		}
		setErrorStyle();
	    super.addKeyUpHandler(new TextCheckHandler());
	    super.addValueChangeHandler(new TextChangedCheckHandler());
	}
	
	/**
	 * Constructor that sets the {@link #errorTitle} to the parameter <code>errorTitle</code> and
	 * the {@link #validatingRegularExpression} to the parameter <code>validatingRegularExpression</code>.
	 * Sets the property to enable or disable the paste.
	 * Turns red the border.
	 * @param validatingRegularExpression the regular expression to be used for validation.
	 * @param errorTitle the title in the case the text is not valid.
	 */
	public RegExValidatingTextBox(String validatingRegularExpression, String errorTitle){
		this(errorTitle, validatingRegularExpression, DEFAULT_IS_PASTE_ENABLED);
	}

	/**
	 * Constructor that sets the title to {@link #TITLE} and
	 * the <code>required</code> property that turns red the border 
	 * if the field is not filled.
	 * @param validatingRegularExpression the regular expression to be used for validation.
	 */
	public RegExValidatingTextBox(String validatingRegularExpression){
		this(validatingRegularExpression, null, DEFAULT_IS_PASTE_ENABLED);
	}
	
	/**
	 * Handler that disables paste action inside the <code>TextBox</code>.
	 * 
	 * @see Event
	 * @see Event#ONPASTE
	 */
	public void onBrowserEvent(Event event) {
	    super.onBrowserEvent(event);
	    if (event.getTypeInt() == Event.ONPASTE) {
			event.stopPropagation();
			event.preventDefault();
	    }
	}
	
	/**
	 * Sets regular exspression
	 * @param validatingRegularExpression regex to use for matching
	 */
	public void setValidatingRegularExpression(String validatingRegularExpression){
		this.validatingRegularExpression = validatingRegularExpression;
	}
	
	/**
	 * Returns <code>true</code> if regex matches with text
	 * @return <code>true</code> if regex matches with text
	 */
	public boolean isValidText(){
		return getText().matches(this.validatingRegularExpression);
	}

	/**
	 * Returns <code>true</code> if regex matches with value
	 * @return <code>true</code> if regex matches with value
	 */
	public boolean isValidValue(){
		return getValue().matches(this.validatingRegularExpression);
	}
	
	/**
	 * Overrides {@link TextBox#setText(String)} checking the style to be used.
	 */
	@Override
	public void setText(String text){
		super.setText(text);
		checkTextStyle();
	}
	
	/**
	 * Overrides {@link TextBox#setValue(String)} checking the style to be used.
	 */
	@Override
	public void setValue(String value){
		super.setValue(value);
		checkValueStyle();
	}
	
	private void checkTextStyle(){
		if (super.getText() != null && this.isValidText()) {
			setOkStyle();
		} else {
			setErrorStyle();
		}
	}
	
	private void checkValueStyle(){
		if (super.getValue() != null && this.isValidValue()) {
			setOkStyle();
		} else {
			setErrorStyle();
		}
	}
	
	@Override
	public void setTitle(String value){
		if (isPasteEnabled) {
			this.okTitle = value;
		} else {
			this.okTitle = value + PASTE_DISABLED_MESSAGE;
		}
		checkTextStyle();
	}
	
	/**
	 * Sets the error style: <br>
	 * turns red the border and sets the 
	 * title to {@link #TITLE}.
	 */
	protected void setErrorStyle(){
		if (null != this.errorTitle) {
			super.setTitle(this.errorTitle);		
		} else {
			super.setTitle(this.okTitle);
		}
		addStyleName(Styles.INSTANCE.textBox().mandatoryError());
	}
	
	/**
	 * Sets the ok style: <br>
	 * normal border no title. 
	 */
	protected void setOkStyle(){
		super.setTitle(okTitle);
		removeStyleName(Styles.INSTANCE.textBox().mandatoryError());
	}

	/**
	 * Handler to check if the text of the <code>TextBox</code>
	 * is valid for the regular expression {@link #validatingRegularExpression}. 
	 * If not, turns red the border and sets the 
	 * title to {@link #.errorTitle} if present.
	 * 
	 * @see KeyUpHandler
	 * @author Alessandro Zambrini
	 */
	private class TextCheckHandler implements KeyUpHandler{
		@Override
		public void onKeyUp(KeyUpEvent event) {
			checkTextStyle();
		}
	}
	
	private class TextChangedCheckHandler implements ValueChangeHandler<String>{
		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			checkTextStyle();
		}
	}

}