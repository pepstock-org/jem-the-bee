/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.commands.docker;

import org.pepstock.jem.commands.Parameter;

/**
 * Instance of a parameter, which wraps the parameter definition and value passed by command line 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class CreateNodeArgument {

	private Parameter parameter = null;
	
	private String value = null;
	
	private boolean required = false;

	/**
	 * Creates an argument using parameter info
	 * @param parameter info about argument
	 */
	public CreateNodeArgument(Parameter parameter) {
		this(parameter, false);
	}

	/**
	 * Creates an argument using parameter info and if is mandatory
	 * @param parameter info about argument
	 * @param required <code>true</code> if parameter is mandatory
	 */
	public CreateNodeArgument(Parameter parameter, boolean required) {
		super();
		this.parameter = parameter;
		this.required = required;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the parameter
	 */
	public Parameter getParameter() {
		return parameter;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubmitArgument [parameter=" + parameter + ", value=" + value + ", required=" + required + "]";
	}
}
