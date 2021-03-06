/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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
import com.google.gwt.user.client.ui.PasswordTextBox;

/**
 * This is an extension of {@link PasswordTextBox} useful for mandatory
 * password in the forms.
 * It displays that the password must be filled and
 * the border turns red if the password is not filled.
 * 
 * @see #TITLE
 * @see PasswordTextBox
 * @author Alessandro Zambrini
 *
 */
public final class MandatoryPasswordTextBox extends PasswordTextBox{

	/**
	 * Title shown if the field password is missing.
	 */
	protected static final String TITLE = "Please fill out the password.";
	
	static{
		Styles.INSTANCE.textBox().ensureInjected();
	}
	
	/**
	 * Constructor that sets the title to {@link #TITLE} and
	 * the <code>required</code> property that turns red the border 
	 * if the field is not filled.
	 */
	public MandatoryPasswordTextBox(){
		super();
		setMandatoryErrorStyle();
	    super.addKeyUpHandler(new TextCheckHandler());
	}
	
	/**
	 * Overrides {@link PasswordTextBox#setText(String)} checking the style to be used.
	 */
	public void setText(String text){
		super.setText(text);
		if(text != null && !text.trim().isEmpty()) {
			setMandatoryOkStyle();
		} else {
			setMandatoryErrorStyle();
		}
	}
	
	/**
	 * Overrides {@link PasswordTextBox#setValue(String)} checking the style to be used.
	 */
	@Override
	public void setValue(String value){
		super.setValue(value);
		if(value != null && !value.trim().isEmpty()) {
			setMandatoryOkStyle();		
		} else {
			setMandatoryErrorStyle();
		}
	}
	
	/**
	 * Sets the mandatory error style: <br>
	 * turns red the border and sets the 
	 * title to {@link #TITLE}.
	 */
	protected void setMandatoryErrorStyle(){
		addStyleName(Styles.INSTANCE.textBox().mandatoryError());
	    this.setTitle(TITLE);		
	}
	
	/**
	 * Sets the mandatory ok style: <br>
	 * normal border no title. 
	 */
	protected void setMandatoryOkStyle(){
		setTitle(null);
		removeStyleName(Styles.INSTANCE.textBox().mandatoryError());
	}

	/**
	 * @return <code>true</code> if has text, <code>false</code> otherwhise
	 */
	public boolean isCompiled() {
		return !getText().trim().isEmpty();
	}
	
	/**
	 * Handler to check if the text of the <code>TextBox</code>
	 * is filled. If not, turns red the border and sets the 
	 * title to {@link #TITLE}.
	 * 
	 * @see KeyUpHandler
	 * @author Alessandro Zambrini
	 */
	private class TextCheckHandler implements KeyUpHandler{
		@Override
		public void onKeyUp(KeyUpEvent event) {
			if (getText().trim().isEmpty()) {
				setMandatoryErrorStyle();
			} else {
				setMandatoryOkStyle();
			}
		}
	}
}