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
package org.pepstock.jem.junit.test.jms;

import java.util.concurrent.Future;

import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.init.JemTestManager;

import junit.framework.TestCase;

/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class Producer extends TestCase{

	/**
	 * Connect to a queue and produce a message
	 * 
	 * @throws Exception
	 */
	public void testProduceMessage() throws Exception {
		// clean jobs
		Future<SubmitResult> future = JemTestManager.getSharedInstance()
				.submit(getJcl("TEST_JMS_ANT_PRODUCE_MESSAGE.xml"), "ant", true,
						false);
		SubmitResult sr = future.get();
		assertEquals(sr.getRc(), 0);
	}

	private String getJcl(String name) {
		return this.getClass().getResource("jcls/" + name).toString();
	}
}
