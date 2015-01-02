/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
import java.text.MessageFormat;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.JemFilterFields;
import org.pepstock.jem.util.filters.fields.JobFilterFields;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

/**
 * This predicate is used to filter the nodes to extract distributing all searches on all nodes of JEM.
 * <br>
 * The {@link Predicate} of a {@link Job}
 * @author Marco "Fuzzo" Cuccato
 * @version 1.4
 *
 */
public class JobPredicate extends JemFilterPredicate<Job> implements Serializable {

	private static final long serialVersionUID = 7910310173201523940L;
	
	/**
	 * Empty constructor
	 */
	public JobPredicate() {
	}
	
	/**
	 * Constructs the object saving the filter to use to extract the jobs
	 * from Hazelcast map
	 * @see JemFilterPredicate
	 * @param filter string filter
	 */
	public JobPredicate(Filter filter) {
		super(filter);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.filters.predicates.JemFilterPredicate#apply(com.hazelcast.core.MapEntry)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean apply(MapEntry entry) {
		// map entry value
		Job job = (Job)entry.getValue();
		
		// initial flag, this should be invalidated if some checks fail
		boolean includeThis = true;
		
		// iterate over all filter tokens
		FilterToken[] tokens = getFilter().toTokenArray();
		// exit if tokens already processed OR if i can immediate exclude this
		for (int i=0; i<tokens.length && includeThis; i++) {
			FilterToken token = tokens[i];
			// gets name and value
			// remember that filters are built:
			// -[name] [value]
			String tokenName = token.getName();
			String tokenValue = token.getValue();
			// gets the filter field for jobs by name
			JobFilterFields field = JobFilterFields.getByName(tokenName);
			// if field is not present,
			// used NAME as default
			if (field == null) {
				// this is the default field for Job
				field = JobFilterFields.NAME;
			}
			// based on name of field, it will check
			// different attributes 
			// all matches are in AND
			switch (field) {
			case NAME:
				// checks name of JOB
				includeThis &= checkName(tokenValue, job);
				break;
			case TYPE:
				// checks type of JOB
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getType(), tokenValue);
				break;
			case USER:
				// checks user (the surrogated as weel) of JOB
				includeThis &= job.isUserSurrogated() ? StringUtils.containsIgnoreCase(job.getJcl().getUser(), tokenValue) : StringUtils.containsIgnoreCase(job.getUser(), tokenValue);
				break;
			case ENVIRONMENT:
				// checks environment of JOB
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getEnvironment(), tokenValue);
				break;
			case DOMAIN:
				// checks domain of JOB
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getDomain(), tokenValue);
				break;
			case AFFINITY:
				// checks affinity of JOB
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getAffinity(), tokenValue);
				break;
			case SUBMITTED_TIME:
				// checks the submitted time of JOB
				includeThis &= checkTime(tokenValue, job.getSubmittedTime());
				break;
			case PRIORITY:
				// checks the priority of JOB
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(job.getJcl().getPriority()), tokenValue);
				break;
			case MEMORY:
				// checks the memory requested of JOB
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(job.getJcl().getMemory()), tokenValue);
				break;
			case STEP:
				// checks the current step of JOB
				includeThis &= StringUtils.containsIgnoreCase(job.getCurrentStep().getName(), tokenValue);
				break;
			case RUNNING_TIME:
				// checks the running time of JOB
				includeThis &= checkTime(tokenValue, job.getStartedTime());
				break;
			case MEMBER:
				// checks the JEM node where the job is executing
				includeThis &= StringUtils.containsIgnoreCase(job.getMemberLabel(), tokenValue);
				break;
			case ENDED_TIME:
				// checks the ended time of JOB
				includeThis &= checkTime(tokenValue, job.getEndedTime());
				break;
			case RETURN_CODE:
				// checks the return code of JOB
				includeThis &= checkReturnCode(tokenValue, job);
				break;
			case ID:
				// checks the ID of JOB
				includeThis &= StringUtils.containsIgnoreCase(job.getId(), tokenValue);
				break;
				// checks JOB is routed
			case ROUTED:
				boolean wantRouted = tokenValue.trim().equalsIgnoreCase(JemFilterFields.YES);
				boolean isRouted = job.getRoutingInfo().getRoutedTime() != null; 
				includeThis &= wantRouted == isRouted;
				break;
			default:
				// otherwise it uses a wrong filter name
				throw new JemRuntimeException("Unrecognized Job filter field: " + field);
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
	private boolean checkName(String tokenValue, Job job){
		// is able to manage for job name the * wildcard
		// matches ALWAYS if has got the star only
		if ("*".equalsIgnoreCase(tokenValue)) {
			return true;
		} else {
			// checks if ends with wildcard
			if (tokenValue.endsWith("*")){
				// if yes, remove the stars
				String newTokenValue = StringUtils.substringBeforeLast(tokenValue, "*");
				// and compares if the value is in the job name
				return StringUtils.containsIgnoreCase(job.getName(), newTokenValue);
			} else {
				// testif a job id has been inserted
				MessageFormat jobIdFormat = new MessageFormat(Factory.JOBID_FORMAT);
				// checks if is by job id
				try {
					// try to parse the job id
					jobIdFormat.parse(tokenValue);
					// checks if the ID is the same
					return StringUtils.containsIgnoreCase(job.getId(), tokenValue);
				} catch (ParseException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// if here means that is not a JOB ID
					// then it uses the job name
					return StringUtils.containsIgnoreCase(job.getName(), tokenValue);
				}
			}
		}		
	}
	
	/**
	 * Checks return code of job
	 * @param tokenValue filter of return code
	 * @param job job instance
	 * @return true if matches
	 */
	private boolean checkReturnCode(String tokenValue, Job job){
		try {
			// parses the integer passed by filter value
			int inputReturnCode = Integer.parseInt(tokenValue);
			// compares job return code with filter value
			return job.getResult().getReturnCode() == inputReturnCode;
		} catch (Exception e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// always false
			return false;
		}
	}
}