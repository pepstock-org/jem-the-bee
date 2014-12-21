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
package org.pepstock.jem.springbatch.tasks;

import java.util.LinkedList;
import java.util.List;

/**
 * Singleton which isloaded with all beans and chunks defined inside of Spring Batch.
 * <br>
 * This singleton is necessary when you should manager different lock scope of job. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class DefinitionsContainer {
	
	private static DefinitionsContainer INSTANCE = null;
	
	private List<Definition> objects = new LinkedList<Definition>();

	/**
	 * Singleton, emtpy constructor
	 */
	private DefinitionsContainer() {
	}
	
	/**
	 * @return singleton instance
	 */
	public static synchronized DefinitionsContainer getInstance(){
		// if null, is the first call
		// then instantiate it
		if  (INSTANCE == null){
			INSTANCE = new DefinitionsContainer();
		}
		return INSTANCE;
	}

	/**
	 * @return the tasklets
	 */
	List<Definition> getObjects() {
		return objects;
	}

	/**
	 * @param objects the tasklets to set
	 */
	void setObjects(List<Definition> objects) {
		this.objects = objects;
	}
}