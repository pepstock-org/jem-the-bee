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

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.UpdateNode;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.affinity.Result;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NodesManagerTest extends TestCase {
	
	private static NodeInfoBean node = null;

	public void testGetNodesByFilter() throws Exception {		
		Collection<NodeInfoBean> all = RestManager.getInstance().getNodesManager().getNodesByFilter("*");
		if (all != null && !all.isEmpty()){
			for (NodeInfoBean entry : all){
				System.out.println(entry);
			}
			return;
		}
		throw new Exception("Unable to get nodes");
	}
	
	public void testGetSwarmNodes() throws Exception {	
		Collection<NodeInfoBean> all = RestManager.getInstance().getNodesManager().getSwarmNodes("*");
		if (all != null && !all.isEmpty()){
			for (NodeInfoBean entry : all){
				System.out.println(entry);
			}
		}
	}
	
	public void testGetNodes() throws Exception {
		Collection<NodeInfoBean> all = RestManager.getInstance().getNodesManager().getNodes("*");
		if (all != null && !all.isEmpty()){
			for (NodeInfoBean entry : all){
				if (node == null){
					node = entry;
				}
				System.out.println(entry);
			}
		}
		if (node == null){
			throw new Exception("Unable to get the node");
		}
		String top = RestManager.getInstance().getNodesManager().top(node.getKey());
		assertNotNull(top);
		String log = RestManager.getInstance().getNodesManager().log(node.getKey());
		assertNotNull(log);
		String cluster = RestManager.getInstance().getNodesManager().displayCluster(node.getKey());
		assertNotNull(cluster);
		ConfigurationFile file = RestManager.getInstance().getNodesManager().getAffinityPolicy(node.getKey());
		assertNotNull(file);
		assertNotNull(file.getContent());
		Result result = RestManager.getInstance().getNodesManager().checkAffinityPolicy(node.getKey(), file.getContent());
		assertNotNull(result);
		file = RestManager.getInstance().getNodesManager().putAffinityPolicy(node.getKey(), file.getContent());
		assertNotNull(file);
		assertNotNull(file.getContent());
		UpdateNode update = new UpdateNode();
		update.setDomain("NewDomain");
		update.setParallelJobs(10);
		boolean updated = RestManager.getInstance().getNodesManager().update(node.getKey(), update);
		assertEquals(updated, true);
		
		NodeInfoBean newNode = RestManager.getInstance().getNodesManager().getNode(node.getKey());
		assertNotNull(newNode);
		if (!newNode.getExecutionEnvironment().getDomain().equalsIgnoreCase("NewDomain")){
			throw new Exception("Unable to update node");
		}
		update.setDomain(node.getExecutionEnvironment().getDomain());
		updated = RestManager.getInstance().getNodesManager().update(node.getKey(), update);
		assertEquals(updated, true);

		updated = RestManager.getInstance().getNodesManager().drain(node.getKey());
		assertEquals(updated, true);
		Thread.sleep(2000);
		
		newNode = RestManager.getInstance().getNodesManager().getNode(node.getKey());
		assertNotNull(newNode);
		if (!newNode.getStatus().equalsIgnoreCase(Status.DRAINED.getDescription())){
			throw new Exception("Unable to drain node");
		}
		updated = RestManager.getInstance().getNodesManager().start(node.getKey());
		assertEquals(updated, true);
		Thread.sleep(2000);
		
		newNode = RestManager.getInstance().getNodesManager().getNode(node.getKey());
		assertNotNull(newNode);
		if (newNode.getStatus().equalsIgnoreCase(Status.DRAINED.getDescription())){
			throw new Exception("Unable to start node");
		}

	}
}
