/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco "Cuc" Cuccato
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
	 * @see JemFilterPredicate
	 * @param filter 
	 */
	public NodePredicate(Filter filter) {
		super(filter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean apply(MapEntry entry) {
		NodeInfoBean node = ((NodeInfo)entry.getValue()).getNodeInfoBean();
		boolean includeThis = true;
		FilterToken[] tokens = getFilter().toTokenArray();
		for (int i=0; i<tokens.length && includeThis; i++) {
			FilterToken token = tokens[i];
			String tokenName = token.getName();
			String tokenValue = token.getValue();
			NodeFilterFields field = NodeFilterFields.getByName(tokenName);
			if (field == null) {
				field = NodeFilterFields.NAME;
			}
			
			switch (field) {
			case NAME:
				includeThis &= checkName(tokenValue, node);
				break;
			case HOSTNAME:
				includeThis &= StringUtils.containsIgnoreCase(node.getHostname(), tokenValue);
				break;
			case DOMAIN:
				includeThis &= StringUtils.containsIgnoreCase(node.getExecutionEnvironment().getDomain(), tokenValue);
				break;
			case STATIC_AFFINITIES:
				includeThis &= StringUtils.containsIgnoreCase(node.getExecutionEnvironment().getStaticAffinities().toString(), tokenValue);
				break;
			case DYNAMIC_AFFINITIES:
				includeThis &= StringUtils.containsIgnoreCase(node.getExecutionEnvironment().getDynamicAffinities().toString(), tokenValue);
				break;
			case STATUS:
				// skipped status == null check
				includeThis &= StringUtils.containsIgnoreCase(node.getStatus(), tokenValue);
				break;
			case OS:
				includeThis &= StringUtils.containsIgnoreCase(node.getSystemName(), tokenValue);
				break;
			case MEMORY:
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(node.getExecutionEnvironment().getMemory()), tokenValue);
				break;
			case PARALLEL_JOBS:
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
				throw new JemRuntimeException("Unrecognized Node filter field: " + field);
			}
		}
		return includeThis;
	}
	
	/**
	 * Checks the filter name
	 * @param tokenValue filter passed
	 * @param job job instance
	 * @return true if matches
	 */
	private boolean checkName(String tokenValue, NodeInfoBean node){
		// is able to manage for label the * wildcard
		if ("*".equalsIgnoreCase(tokenValue)) {
			return true;
		} else {
			String newTokenValue = tokenValue;
			if (tokenValue.endsWith("*")){
				newTokenValue = StringUtils.substringBeforeLast(tokenValue, "*");
			}
			return StringUtils.containsIgnoreCase(node.getLabel(), newTokenValue);
		}		
	}

}