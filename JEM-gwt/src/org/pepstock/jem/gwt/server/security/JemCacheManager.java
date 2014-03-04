/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.server.security;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.pepstock.jem.gwt.server.commons.SharedObjects;

/**
 * Cache Manager for SHIRO. Creates ALWAYS a JemCache!
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class JemCacheManager implements CacheManager {
	
	@SuppressWarnings("rawtypes")
	private static JemCache jemCache;
	
	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.CacheManager#getCache(java.lang.String)
	 */
    @SuppressWarnings("unchecked")
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
    	if(jemCache==null){
    		jemCache=new JemCache<K, V>(name);
    		return jemCache;
    	}
    	return jemCache;
	}
    
    public static void updateJemCache(){
		// checks if hazeclast is available
		if (SharedObjects.getInstance().isDataClusterAvailable()){
			jemCache.setHazelcastCache();
		} else {
			jemCache.setMemoryCache();
		}
    }
}