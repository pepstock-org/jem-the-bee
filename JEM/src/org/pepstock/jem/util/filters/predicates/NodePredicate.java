/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Cuc" Cuccato
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
package org.pepstock.jem.util.filters.predicates;


import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.NodeFilterFields;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

/**
 * This predicate is used to filter the nodes to extract distributing all searches on all nodes of JEM.
 * <br>
 * The {@link Predicate} of a {@link NodeInfoBean}
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 */
public class NodePredicate extends JemFilterPredicate<NodeInfoBean> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public NodePredicate() {
	}
	
	/**
	 * Constructs the object saving the filter to use to extract the nodes
	 * from Hazelcast map
	 * @see JemFilterPredicate
	 * @param filter string filter
	 */
	public NodePredicate(Filter filter) {
		super(filter);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.filters.predicates.JemFilterPredicate#apply(com.hazelcast.core.MapEntry)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean apply(MapEntry entry) {
		// casts the object to a NodeInfo
		NodeInfoBean node = ((NodeInfo)entry.getValue()).getNodeInfoBean();
		boolean includeThis = true;
		// gets all tokens of filter
		FilterToken[] tokens = getFilter().toTokenArray();
		// scans all tokens
		for (int i=0; i<tokens.length && includeThis; i++) {
			FilterToken token = tokens[i];
			// gets name and value
			// remember that filters are built:
			// -[name] [value]
			String tokenName = token.getName();
			String tokenValue = token.getValue();
			// gets the filter field for nodes by name
			NodeFilterFields field = NodeFilterFields.getByName(tokenName);
			// if field is not present,
			// used NAME as default
			if (field == null) {
				field = NodeFilterFields.NAME;
			}
			// based on name of field, it will check
			// different attributes 
			// all matches are in AND
			switch (field) {
			case NAME:
				// checks name of NODE
				includeThis &= checkName(tokenValue, node.getLabel());
				break;
			case HOSTNAME:
				// checks hostname or ip of NODE
				includeThis &= StringUtils.containsIgnoreCase(node.getHostname(), tokenValue);
				break;
			case DOMAIN:
				// checks domain of NODE
				includeThis &= StringUtils.containsIgnoreCase(node.getExecutionEnvironment().getDomain(), tokenValue);
				break;
			case STATIC_AFFINITIES:
				// checks static affinities of NODE
				includeThis &= StringUtils.containsIgnoreCase(node.getExecutionEnvironment().getStaticAffinities().toString(), tokenValue);
				break;
			case DYNAMIC_AFFINITIES:
				// checks dinamic affinities of NODE
				includeThis &= StringUtils.containsIgnoreCase(node.getExecutionEnvironment().getDynamicAffinities().toString(), tokenValue);
				break;
			case STATUS:
				// skipped status == null check
				includeThis &= StringUtils.containsIgnoreCase(node.getStatus(), tokenValue);
				break;
			case OS:
				// checks operating system of NODE
				includeThis &= StringUtils.containsIgnoreCase(node.getSystemName(), tokenValue);
				break;
			case MEMORY:
				// checks memory of NODE
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(node.getExecutionEnvironment().getMemory()), tokenValue);
				break;
			case PARALLEL_JOBS:
				// checks parallel jobs of NODE
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(node.getExecutionEnvironment().getParallelJobs()), tokenValue);
				break;
			case CURRENT_JOB:
				// skipped jobName == null check
				includeThis &= StringUtils.containsIgnoreCase(node.getJobNames().toString() , tokenValue);
				break;
			case ENVIRONMENT:
				// skipped jobName == null check
				includeThis &= StringUtils.containsIgnoreCase(node.getExecutionEnvironment().getEnvironment(), tokenValue);
				break;
			default:
				// otherwise it uses a wrong filter name
				throw new JemRuntimeException("Unrecognized Node filter field: " + field);
			}
		}
		return includeThis;
	}
}