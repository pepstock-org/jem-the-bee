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
package org.pepstock.jem.junit.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.pepstock.jem.junit.test.antutils.AntUtilsSuite;
import org.pepstock.jem.junit.test.common.CommonSuite;
import org.pepstock.jem.junit.test.springbatch.SpringBatchSuite;

/**
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
@RunWith(Suite.class)
@SuiteClasses({ CommonSuite.class, AntUtilsSuite.class, SpringBatchSuite.class })
public class JemTestSuite {

}
