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
package org.pepstock.jem.node.listeners;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.util.Numbers;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.MapStore;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class DataLossManager {
	
	private static final DataLossManager INSTANCE = new DataLossManager();

	private static final String KEY_SEPARATOR = "-";
	
	private static final long PERIOD = Numbers.N_2 * TimeUtils.SECOND;
	
	private static final long CHECK = Numbers.N_4 * TimeUtils.SECOND;
	
	private AtomicBoolean IS_RUNNIG = new AtomicBoolean(false);

	private Map<String, Long> partitions = new ConcurrentHashMap<String, Long>();
	
	private Set<String> partitionsToRemove = new HashSet<String>();

	private ExecutorService SERVICE = Executors.newFixedThreadPool(1);
	

	/**
	 * 
	 */
	private DataLossManager() {
	}
	
	static synchronized DataLossManager getInstance(){
		return INSTANCE;
	}
	
	synchronized void add(String memberId, String map){
		partitions.put(memberId + KEY_SEPARATOR + map, System.currentTimeMillis());
		if (!IS_RUNNIG.get() && Main.IS_COORDINATOR.get()){
			IS_RUNNIG.set(true);
			SERVICE.execute(new DataLossChecker());
		}
	}
	
	class DataLossChecker extends TimerTask {

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			while(!partitions.isEmpty()){
				for (Entry<String, Long> entry : partitions.entrySet()){
					long time = entry.getValue();
					long timeTocheck = System.currentTimeMillis() - CHECK; 
					if (timeTocheck > time){
						String map = StringUtils.substringAfterLast(entry.getKey(), "-");
						load(map);
						partitionsToRemove.add(entry.getKey());
					}
				}
				for (String key : partitionsToRemove){
					partitions.remove(key);
				}
				partitionsToRemove.clear();
				try {
					Thread.sleep(PERIOD);
				} catch (InterruptedException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
			IS_RUNNIG.set(false);
		}
		
		/**
		 * Retrieves all keys from database if map has got mapstore
		 */
		private void load(String  map) {
			LogAppl.getInstance().emit(NodeMessage.JEMC219I, map);
			// gets current time
			long start = System.currentTimeMillis();
			// gets from all HC config 
			MapConfig mapConfig = Main.getHazelcast().getConfig().getMapConfig(map);
			if (mapConfig != null){
				// gets mapstore config
				MapStoreConfig mStoreConfig = mapConfig.getMapStoreConfig();
				// checks if mapstore is enabled
				if (mStoreConfig != null && mStoreConfig.isEnabled()) {
					// loads the mapp store defined for the map
					@SuppressWarnings({ "unchecked" })
					MapStore<String, Object> clazz = (MapStore<String, Object>) mStoreConfig.getImplementation();
					// gets all keys
					Iterable<String> keys = clazz.loadAllKeys();
					LogAppl.getInstance().emit(NodeMessage.JEMC220I, map);
					// the best way to reload the data, is the "containsKey" the object
					// from the map by key. If is not in memory, HC reloads from
					// mapstore and then from database
					for (String storeKey : keys) {
						try {
							// lock
							Main.getHazelcast().getMap(map).lock(storeKey);
							// check if in map (if not in memory, HC askes to map store to load from DB)
							Main.getHazelcast().getMap(map).containsKey(storeKey);
						} finally {
							// always unlock
							Main.getHazelcast().getMap(map).unlock(storeKey);
						}
					}
				}
			}
			long end = System.currentTimeMillis() - start;
			// shows the elapsed time
			LogAppl.getInstance().emit(NodeMessage.JEMC221I, end, map);
		}

	}

}
