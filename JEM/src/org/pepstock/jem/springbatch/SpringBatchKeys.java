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
package org.pepstock.jem.springbatch;

/**
 * Contains all constants used inside of SpringBatch source code (often as key
 * of beans or properties) to be compliance JEM .
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class SpringBatchKeys {

	/**
	 * Is the mandatory <code>id</code> attribute value for JemBean, inside of
	 * SpringBatch source code
	 */
	public static final String BEAN_ID = "jem.bean";

	/**
	 * Is the mandatory <code>id</code> attribute value for job and step
	 * listener, inside of SpringBatch source code
	 */
	public static final String LISTENER_ID = "jem.listener";

	/**
	 * Is the mandatory <code>id</code> attribute value for job element, used on
	 * real job element to extends the listener already defined (see
	 * <code>parent</code> attribute)
	 */
	public static final String BASE_JOB_ID = "jem.baseJob";

	/**
	 * Is the mandatory <code>id</code> attribute value for step element, used
	 * on real step element to extends the listener already defined (see
	 * <code>parent</code> attribute)
	 */
	public static final String BASE_STEP_ID = "jem.baseStep";

	/**
	 * Is the mandatory <code>id</code> attribute value for property element
	 * inside of bean element for JemBean, to set job Name. Be careful because
	 * jobname is used to call SpringBatch command line
	 */
	public static final String JOB_NAME = "jobName";

	/**
	 * Is <code>job</code> tag, searched to extract job name. if SpringBatch
	 * framework change it, must be changed
	 */
	public static final String JOB_TAG = "job";

	/**
	 * Is <code>bean</code> tag, searched to extract JemBean. if Spring
	 * framework change it, must be changed
	 */
	public static final String BEAN_TAG = "bean";

	/**
	 * Is <code>property</code> tag, searched to load JemBean. if Spring
	 * framework change it, must be changed
	 */
	public static final String PROPERTY_TAG = "property";

	/**
	 * Is <code>id</code> attribute, used on all Spring elements. if Spring
	 * framework change it, must be changed
	 */
	public static final String ID_ATTR = "id";

	/**
	 * Is <code>abstract</code> attribute, used on <code>job</code> element if
	 * you want to have a generic job, with same attributes for all job. if
	 * Spring framework change it, must be changed
	 */
	public static final String ABSTRACT_ATTR = "abstract";

	/**
	 * Is <code>name</code> attribute, used on all Spring <code>property</code>
	 * elements. if Spring framework change it, must be changed
	 */
	public static final String NAME_ATTR = "name";

	/**
	 * Is <code>value</code> attribute, used on all Spring <code>property</code>
	 * elements. if Spring framework change it, must be changed
	 */
	public static final String VALUE_ATTR = "value";
	
	/**
	 * Is constant to define locking scope to job level
	 */
	public static final String JOB_SCOPE = "job";

	/**
	 * Is constant to define locking scope to step level
	 */
	public static final String STEP_SCOPE = "step";

	/**
	 * To avoid any instantiation
	 */
	private SpringBatchKeys() {
	}
	
	
}