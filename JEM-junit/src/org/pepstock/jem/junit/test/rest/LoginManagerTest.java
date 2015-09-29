/**
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pepstock.jem.junit.test.rest;

import junit.framework.TestCase;

import org.pepstock.jem.node.security.LoggedUser;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class LoginManagerTest extends TestCase {

	public void testSavePreferences() throws Exception {
			LoggedUser user = RestManager.getInstance().getLoginManager().getUser();
			RestManager.getInstance().getLoginManager().storePreferences(user.getPreferences());
	}

}
