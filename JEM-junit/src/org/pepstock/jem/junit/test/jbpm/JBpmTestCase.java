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
package org.pepstock.jem.junit.test.jbpm;

import org.pepstock.jem.jbpm.JBpmFactory;
import org.pepstock.jem.junit.test.JemTestCase;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JBpmTestCase extends JemTestCase {

	/* (non-Javadoc)
	 * @see org.pepstock.jem.junit.test.JemTestCase#getType()
	 */
    @Override
    public String getType() {
	    return JBpmFactory.JBPM_TYPE;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.junit.test.JemTestCase#getTestCaseClass()
	 */
    @Override
    public Class<?> getTestCaseClass() {
	    return JBpmTestCase.class;
    }


}
