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

import java.util.Collection;

import junit.framework.TestCase;

import org.pepstock.jem.node.stats.LightSample;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StatisticsManagerTest extends TestCase {

	public void testGetSamples() throws Exception {
			Collection<LightSample> all = RestManager.getInstance().getStatisticsManager().getSamples();
			if (all != null && !all.isEmpty()){
				for (LightSample entry : all){
					System.out.println(entry);
				}
				return;
			}
			throw new Exception("Unable to get samples");
	}

	public void testGetCurrentSample() throws Exception {
			assertNotNull(RestManager.getInstance().getStatisticsManager().getCurrentSample());
	}
	public void testGetAbout() throws Exception {
		assertNotNull(RestManager.getInstance().getStatisticsManager().getAbout());
	}

	public void testDesiplayRequesters() throws Exception {
		assertNotNull(RestManager.getInstance().getStatisticsManager().displayRequestors("*"));
	}
	public void testGetAllRedoStatements() throws Exception {
		assertNotNull(RestManager.getInstance().getStatisticsManager().getAllRedoStatements());	
	}
	public void testGetInfos() throws Exception {
		assertNotNull(RestManager.getInstance().getStatisticsManager().getEnvironmentInformation());	
	}
}
