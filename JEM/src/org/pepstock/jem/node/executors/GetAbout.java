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
package org.pepstock.jem.node.executors;

import java.util.Collection;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.About;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeLicense;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;

/**
 * Extracts licenses info if exist.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 * 
 */
public class GetAbout extends DefaultExecutor<About>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Calls an executor to extract all licences information, only if enterprise
	 * configuration is running
	 * @return bean with all info to show on UI
	 * @throws Exception occurs if errors
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public About execute() throws ExecutorException {
		// I need the reflection to avoid to distribute 
		// create reference on enterprise
		About about = new About();
		try {
			Class clazz = Class.forName("org.pepstock.jem.license.GetLicensesInfo");
			DefaultExecutor<Collection<NodeLicense>> da = (DefaultExecutor<Collection<NodeLicense>>) clazz.newInstance();
			about.getLicenses().addAll(da.execute());
		} catch (ClassNotFoundException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC207E, e);
		} catch (InstantiationException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC207E, e);
		} catch (IllegalAccessException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC207E, e);
		} catch (ExecutorException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC207E, e);
		}
		loadManifest(about);
		return about;
	}
	
	/**
	 * Loads the info which are present in manifst file of JAR  
	 * @param about bean to load
	 */
	private void loadManifest(About about){
		// gets version
    	String version = NodeInfoUtility.getManifestAttribute(ConfigKeys.JEM_MANIFEST_VERSION);
        if (version != null){
        	about.setVersion(version);
        }
		// gets build time
		String creation = NodeInfoUtility.getManifestAttribute(ConfigKeys.JEM_MANIFEST_CREATION_TIME);
		if (creation != null){
			about.setCreationTime(creation);
		}
	}
}