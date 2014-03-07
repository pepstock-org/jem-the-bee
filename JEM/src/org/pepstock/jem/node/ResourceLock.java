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
package org.pepstock.jem.node;

import java.io.Serializable;

/**
 * GRS works only with 2 kinds of lock types: <br>
 * <br>
 * <code><b>READ</b></code>: in read mode, you can access to resource if there
 * are others requesters who asked the same resource. <br>
 * <code><b>WRITE</b></code>: in write mode, you can access to resource if there
 * aren't any requester who asked the same resource. After you have the
 * exclusive access to resource, all other requester will wait that you'll end
 * and unlock that. <br>
 * Here you have to define the lock type and the resource name. A resource is
 * equals to another if it has the same name.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ResourceLock implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Lock type for reading a resource (share lock). This is the default.
	 */
	public static final int READ_MODE = 0;

	/**
	 * Lock type for writing a resource (exclusive lock)
	 */
	public static final int WRITE_MODE = 1;

	private String name = null;

	private int mode = READ_MODE;

	private boolean locked = false;

	/**
	 * Constructs the object with the resource name. Lock type is READ mode for
	 * default.
	 * 
	 * @param name resource name
	 */
	public ResourceLock(String name) {
		this(name, READ_MODE);
	}

	/**
	 * Constructs the object with the resource name and lock type.
	 * 
	 * @param name resource name
	 * @param mode lock type
	 * @throws IllegalArgumentException if resource name is null or if lock type
	 *             is not READ and WRITE
	 */
	public ResourceLock(String name, int mode) {
		// checks name. if null, exception occurs
		if (name == null){
			throw new IllegalArgumentException(NodeMessage.JEMC145E.toMessage().getMessage());
		}
		// checks mode. if not READ and WRITE, exception occurs
		if (mode != READ_MODE && mode != WRITE_MODE){
			throw new IllegalArgumentException(NodeMessage.JEMC146E.toMessage().getMessage());
		}

		// sets name and mode
		this.name = name;
		this.mode = mode;
	}

	/**
	 * Returns the resource name.
	 * 
	 * @return resource name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the resource name. <br>
	 * <b>BE AWARE:</b> Is protected. For all, use the constructor
	 * 
	 * @param name resource name
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the lock type
	 * 
	 * @return lock type
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Sets the lock type <b>BE AWARE:</b> Is protected. For all, use the
	 * constructor
	 * 
	 * @param mode lock type
	 */
	void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Returns the resource LOCKED
	 * 
	 * @return <code>ture</code> if already locked, otherwise false
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Sets the resource LOCKED, so no further operation must be done
	 * 
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Returns the string representation of resource to lock.
	 * 
	 * @return resource to lock string representation
	 */
	@Override
	public String toString() {
		return "Resource [name=" + name + ", mode=" + ((mode == READ_MODE) ? "READ" : "WRITE") + "]";
	}

}