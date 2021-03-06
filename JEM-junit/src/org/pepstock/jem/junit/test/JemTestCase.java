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
package org.pepstock.jem.junit.test;

import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.init.JemTestManager;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public abstract class JemTestCase extends TestCase {

	/**
	 * 
	 */
	public JemTestCase() {
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public final int submit(String name) throws Exception{
		Future<SubmitResult> future = JemTestManager.getSharedInstance().submit(getJcl(name), getType(), true,	false);
		SubmitResult sr = future.get();
		return sr.getRc();
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public final String getJcl(String name) {
		return getTestCaseClass().getResource("jcls/" + name).toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract String getType();
	/**
	 * 
	 * @return
	 */
	public abstract Class<?> getTestCaseClass();
}
