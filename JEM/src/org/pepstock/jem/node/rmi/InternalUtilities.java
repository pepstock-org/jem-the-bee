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
package org.pepstock.jem.node.rmi;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Collection;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.Resource;

/**
 * is RMI interface to have an entry point to perform system command.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public interface InternalUtilities extends CommonResourcer {

	/**
	 * RMI object ID for binding.
	 */
	public static final String NAME = "INTERNAL_UTILITIES";
	
	/**
	 * Asks to drain nodes
	 * 
	 * @param jobId job id which is asking for service
	 * @param nodesPattern nodes pattern to drain
	 * @return number of nodes drained
	 * @throws RemoteException occurs if errors
	 */
	public int drain(String jobId, String nodesPattern) throws RemoteException;
	
	/**
	 * Asks to start nodes
	 * 
	 * @param jobId job id which is asking for service
	 * @param nodesPattern nodes pattern to drain
	 * @return number of nodes started
	 * @throws RemoteException occurs if errors
	 */
	public int start(String jobId, String nodesPattern) throws RemoteException;

	/**
	 * Grants some roles adding specific permissions
	 * 
	 * @param jobId job id which is asking for service
	 * @param permissions permissions to add
	 * @param roles roles names to grant
	 * @throws RemoteException if any error occurs
	 * 
	 */
	public void grant(String jobId, String[] permissions, String[] roles) throws RemoteException;

	/**
	 * Revokes permissions previously granted to roles
	 * 
	 * @param jobId job id which is asking for service
	 * @param permissions permissions to remove
	 * @param roles roles names to revoke
	 * @throws RemoteException if any error occurs
	 * 
	 */
	public void revoke(String jobId, String[] permissions, String[] roles) throws RemoteException;

	/**
	 * Adds new users to roles
	 * 
	 * @param jobId job id which is asking for service
	 * @param users list of users to add
	 * @param roles roles names to be updated
	 * @throws RemoteException if any error occurs
	 * 
	 */
	public void add(String jobId, String[] users, String[] roles) throws RemoteException;

	/**
	 * Deletes users previously added to roles
	 * 
	 * @param jobId job id which is asking for service
	 * @param users list of users to delete
	 * @param roles roles names to be updated
	 * @throws RemoteException if any error occurs 
	 * 
	 */
	public void delete(String jobId, String[] users, String[] roles) throws RemoteException;

	/**
	 * Removes existing roles 
	 * @param jobId job id which is asking for service
	 * @param roles list of roles names to remove
	 * @throws RemoteException if any error occurs
	 * 
	 */
	public void remove(String jobId, String[] roles) throws RemoteException;

	/**
	 * Creates new roles, empty of permissions and users
	 * @param jobId job id which is asking for service
	 * @param roles list of roles names to create
	 * @throws RemoteException if any error occurs
	 * 
	 */
	public void create(String jobId, String[] roles) throws RemoteException;
	
	/**
	 * Returns a list of jobs by a SQL predicate in Hazelcast syntax
	 * @param jobId job id which is asking for service
	 * @param sqlPredicate hazelcast SQL predicate
	 * @return collection of jobs 
	 * @throws RemoteException if any error occurs
	 */
	public Collection<Job> getJobs(String jobId, String sqlPredicate) throws RemoteException;
	
	/**
	 * Purges a job from output queue
	 * @param jobId job id which is asking for service
	 * @param job job instance to purge from output queue
	 * @throws RemoteException if any error occurs
	 */
	public void purge(String jobId, Job job) throws RemoteException;
	
	/**
	 * Adds or updates a common resource
	 * @param jobId job id which is asking for service
	 * @param resource resource instance to add or update
	 * @return <code>true</code> if the resource has been added, otherwise it has been replaced 
	 * @throws RemoteException if any error occurs
	 */
	public boolean put(String jobId, Resource resource) throws RemoteException;
	
	/**
	 * Removes an existing resource by its name
	 * @param jobId job id which is asking for service
	 * @param resourceName name of resource to remove
	 * @return <code>true</code> if the resource has been removed, otherwise it hasn't been
	 * @throws RemoteException if any error occurs
	 */
	public boolean remove(String jobId, String resourceName) throws RemoteException;
	
	/**
	 * Returns a list of resources by filter 
	 * @param jobId job id which is asking for service
	 * @param filter filter of resources 
	 * @return collection of all defined resources
	 * @throws RemoteException if any error occurs
	 */
	public Collection<Resource> values(String jobId, String filter) throws RemoteException;
	
	/**
	 * Decrypts a value using internal key
	 * @param jobId job id which is asking for service
	 * @param value decryptable value object
	 * @return return secret value 
	 * @throws RemoteException if any error occurs
	 */
	public String decrypt(String jobId, CryptedValueAndHash value) throws RemoteException;
	
	/**
	 * Encrypts a value using internal key
	 * @param jobId job id which is asking for service
	 * @param value encryptable value object
	 * @return return a encrypted value
	 * @throws RemoteException
	 */
	public CryptedValueAndHash encrypt(String jobId, String value) throws RemoteException;


	/**
	 * Adds x509 user certificate to keystore with given alias
	 * 
	 * @param certificate the X509 Certificate to add
	 * @param jobId job id which is asking for service
	 * @param alias the alias for the certificate to insert in the keystore
	 * @throws RemoteException if any error occurs
	 * 
	 */
	public void importCertificate(String jobId, byte[] certificate, String alias) throws RemoteException;

	/**
	 * delete alias from keystore
	 * 
	 * @param alias the alias to be deleted
	 * @param jobId job id which is asking for service
	 * @throws RemoteException if any error occurs
	 * 
	 */
	public void deleteCertificate(String jobId, String alias) throws RemoteException;
	
	/**
	 * Returns the file which represents the folder where the node stores stats data 
	 * @return file which represents the folder where the node stores stats data 
	 * @throws RemoteException if any error occurs
	 */
	public File getStatisticsFolder() throws RemoteException;

}