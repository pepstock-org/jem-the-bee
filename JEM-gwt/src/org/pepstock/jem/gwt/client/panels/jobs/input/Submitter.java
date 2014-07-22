/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.panels.jobs.input;

/**
 *Interface thet holds constants for any file submitter 
 * @author Marco "Fuzzo" Cuccato
 */
interface Submitter {

	/**
	 * 
	 */
	String SERVICE_NAME = "submitter";
	
	/**
	 * 
	 */
	String FILE_UPLOAD_FIELD = "jcl";

	/**
	 * 
	 */
	String TYPE_FIELD = "type";

	/**
	 * 
	 */
	String FILE_SIZE_LIMIT = "5 MB";
	
	/**
	 * 
	 */
	String FILE_TYPES = "*.xml;";

}
