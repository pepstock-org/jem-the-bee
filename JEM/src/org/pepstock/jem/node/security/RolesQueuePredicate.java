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
package org.pepstock.jem.node.security;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.Predicates.AbstractPredicate;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.thoughtworks.xstream.XStream;

/**
 * Is a custom predicate (used by Hazelcast to filter object from maps) to extract from roles queue only roles which matches with user or orgUnit.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class RolesQueuePredicate extends AbstractPredicate {

	private static final long serialVersionUID = 1L;

	private User user = null;

	private transient XStream stream = new XStream();

	/**
	 * Empty constructor
	 */
	public RolesQueuePredicate() {
	}



	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}



	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Checks the job passed by Hazelcast matches with Execution Environment of node. <br>
	 * This method is called on all nodes of cluster, where a piece of map is present
	 * 
	 * @see com.hazelcast.query.Predicate#apply(com.hazelcast.core.MapEntry)
	 */
	@Override
	public boolean apply(@SuppressWarnings("rawtypes") Map.Entry arg0) {
		// gets role instance
		Role role = (Role) arg0.getValue();
		// gets all users list
		List<String> users = role.getUsers();
		for (String userRegEx: users){
			Pattern pattern = Pattern.compile(userRegEx, Pattern.CASE_INSENSITIVE);
			// if matches with user id return true!
			// otherwise checks if matches with orgUnit id
			Matcher matcher = pattern.matcher(user.getId());
			
			if (matcher.matches()){
				return true;
			} else if (user.getOrgUnitId() != null){
				// checks if matches with orgUnit if not null
				Matcher matcherOu = pattern.matcher(user.getOrgUnitId());
				if (matcherOu.matches()){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * DeSerializes ExecutionEnviroment from XML  
	 * 
	 * @see com.hazelcast.nio.DataSerializable#readData(java.io.DataInput)
	 */
	@Override
	public void readData(ObjectDataInput arg0) throws IOException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		byte b = arg0.readByte();
		while (b > -1) {
			try {
				ba.write(b);
				b = arg0.readByte();
			} catch (EOFException e) {
				b = -1;
			}
		}
		String ee = new String(ba.toByteArray());
		user = (User) stream.fromXML(ee);
	}

	/**
	 * Serializes ExecutionEnviroment to XML 
	 * 
	 * @see com.hazelcast.nio.DataSerializable#writeData(java.io.DataOutput)
	 */
	@Override
	public void writeData(ObjectDataOutput arg0) throws IOException {
		// replace \n beacause are not supported from serialize engine
		String ee = stream.toXML(user).replace('\n', ' ');
		arg0.writeBytes(ee);
	}



	@Override
	public Set<QueryableEntry> filter(QueryContext arg0) {
		return null;
	}

	
}