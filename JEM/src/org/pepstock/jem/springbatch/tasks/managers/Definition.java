/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.springbatch.tasks.managers;

import javax.naming.InitialContext;

import org.pepstock.jem.springbatch.items.DataDescriptionItem;
import org.pepstock.jem.springbatch.tasks.JemTasklet;


/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Definition {

	private Object object = null;
	
	private String stepName = null;
	
	private InitialContext context = null;
	
	/**
	 * 
	 */
	public Definition() {
	}

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}


	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}


	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}

	/**
	 * @param stepName the stepName to set
	 */
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	/**
	 * @return the context
	 */
	public InitialContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(InitialContext context) {
		this.context = context;
	}

	/**
	 * @return true is object is a instance of JemTasklet
	 */
	public boolean isJemTasklet(){
		return object instanceof JemTasklet;
	}

	/**
	 * @return true is object is a instance of DataDescriptionItem
	 */
	public boolean isChunkItem(){
		return object instanceof DataDescriptionItem;
	}

}