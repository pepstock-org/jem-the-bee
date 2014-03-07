/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.log;

import java.io.Serializable;

/**
 * Interface implemented by all <code>enum</code> classes that contain the list of all the log
 * used inside <b>Jem The Bee</b> program. <br>
 * Each <code>MessageInterface</code> Object corresponds to a {@link Message}, that is, each  <code>enum</code> class
 * that implements <code>MessageInterface</code> contains a list of instances of <code>MessageInterface</code>
 * that wrap a <code>Message</code> log inside the program.
 * 
 * @see Message
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 */
public interface MessageInterface extends Serializable {
	
	/**
	 * It returns the {@link Message} corresponding to a <code>MessageInterface</code> instance.
	 * @return the {@link Message} corresponding to a <code>MessageInterface</code> instance.
	 */
	Message toMessage();

}