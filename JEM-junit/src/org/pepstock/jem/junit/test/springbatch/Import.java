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
package org.pepstock.jem.junit.test.springbatch;

import org.pepstock.jem.ant.AntFactory;


/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class Import extends SpringBatchTestCase {
	
	
	private boolean submitANT = true;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.junit.test.springbatch.SpringBatchTestCase#getType()
	 */
    @Override
    public String getType() {
	    if (submitANT){
	    	return AntFactory.ANT_TYPE;
	    } else {
	    	return super.getType();
	    }
    }

	/**
	 * Test the wait ant utility
	 * 
	 * @throws Exception
	 */
	public void testCopyImport() throws Exception {
		submitANT = true;
		assertEquals(submit("imp/TEST_SPRINGBATCH_COPY_IMPORT.xml"), 0);
	}
	
	/**
	 * Test the wait ant utility
	 * 
	 * @throws Exception
	 */
	public void testImport() throws Exception {
		submitANT = false;
		assertEquals(submit("imp/TEST_SPRINGBATCH_IMPORT.xml"), 0);
	}
	
	/**
	 * Test the wait ant utility
	 * 
	 * @throws Exception
	 */
	public void testDeleteImport() throws Exception {
		submitANT = true;
		assertEquals(submit("imp/TEST_SPRINGBATCH_DELETE_IMPORT.xml"), 0);
		submitANT = false;
	}
}
