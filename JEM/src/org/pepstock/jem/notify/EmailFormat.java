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
package org.pepstock.jem.notify;

/**
 * <code>Enumeration</code> for all possible Email
 * formats: <br>
 * 
 * - <code>TEXT_HTML_FORMAT</code>: to send html email <br>
 * - <code>TEXT_PLAIN_FORMAT</code>: to send only text email
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public enum EmailFormat {
	
    /**
     * 
     */
    TEXT_HTML("text/html"),
    /**
     * 
     */
    TEXT_PLAIN("text/plain");

	/**
	 * Default format value for {@link JemEmail}
	 */
    public static final EmailFormat DEFAULT_FORMAT = TEXT_PLAIN;
    
	/**
	 * The Email format.
	 */
    private String format;
   
    /**
     * Constructor with Email format.
     * 
     * @param format the email format
     */
    private EmailFormat(String format){
    	this.format = format.toUpperCase();
    }

    /**
     * This method returns the <code>String</code> format value of an <code>EmailFormat</code>.
     * 
     * @return the <code>String</code> format value of an <code>EmailFormat</code>
     */
    public String getFormat(){
    	return this.format;
    }

    /**
     * This method returns a <code>String</code> representation of an <code>EmailFormat</code>.
     * 
     * @return a <code>String</code> representation of an <code>EmailFormat</code>
     */
    public String toString(){
    	return "Format: " + this.format;
    }

}