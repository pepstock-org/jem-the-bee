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
package org.pepstock.jem.gwt.server.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Cache used by SHIRO for authorizations. This uses a normal HashMap in memeory if is not able to use Jem group, by hazelcast.
 * If the group is available, uses Hazelcast.<br>
 * Is a Hazelcast listener to understand when to use Hazeclast instead of memory.<br>
 * Blocking the access to web site if JEM group is down, it should never use memory hashmap. 
 * 
 * @author Andrea "Stock" Stocchero
 * @param <K> 
 * @param <V> 
 *
 */
public final class JemCache<K, V> implements Cache<K, V> {

	private String name = null;
	
	private Map<K, V> map = null;
	
	/**
	 * Stored the name of cache and checks if hazelcast is available to use it. Otherwise a memory instance will be used.
	 * 
	 * @param name chace map name
	 * 
	 */
    public JemCache(String name) {
    	this.name = name;
		// checks if hazeclast is available
		if (SharedObjects.getInstance().isDataClusterAvailable()){
			setHazelcastCache();
		} else {
			setMemoryCache();
		}
	}

	/**
	 * @return the cache name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the cache name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#clear()
	 */
	@Override
	public void clear() throws CacheException {
		map.clear();
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#get(java.lang.Object)
	 */
	@Override
	public V get(K arg0) throws CacheException {
		return map.get(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#keys()
	 */
	@Override
	public Set<K> keys() {
		return map.keySet();
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K arg0, V arg1) throws CacheException {
		return map.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#remove(java.lang.Object)
	 */
	@Override
	public V remove(K arg0) throws CacheException {
		return map.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#size()
	 */
	@Override
	public int size() {
		return map.size();
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.Cache#values()
	 */
	@Override
	public Collection<V> values() {
		return map.values();
	}
		
    /**
     * Sets HAZECALST cache. Uses the cache name asked by SHIRO
     */
    @SuppressWarnings("unchecked")
    protected final void setHazelcastCache(){
    	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG020I, "HAZELCAST");
		HazelcastInstance instance = SharedObjects.getInstance().getHazelcastClient();
		map = (IMap<K, V>) instance.getMap(getName());
    }
    
    /**
     * Sets Memory cache!  
     */
    protected final void setMemoryCache(){
    	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG020I, "MEMORY");
		map = new ConcurrentHashMap<K, V>();
    }

}