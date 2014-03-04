/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.tasks;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.pepstock.jem.Result;
import org.pepstock.jem.log.JemException;

/**
 * Simple class which joins Callable and Serializable interfaces, without any
 * implementation of their methods.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class Task implements Callable<Result>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public Task() {
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public abstract Result call() throws JemException;

	
}