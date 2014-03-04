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
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.TimeUtils;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.JobFilterFields;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

/**
 * The {@link Predicate} of a {@link Job}
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
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
	 * @see JemFilterPredicate
	 * @param filter 
	 */
	public JobPredicate(Filter filter) {
		super(filter);
	}

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
			String tokenName = token.getName();
			String tokenValue = token.getValue();
			
			JobFilterFields field = JobFilterFields.getByName(tokenName);
			if (field == null) {
				// this is the default field for Job
				field = JobFilterFields.NAME;
			}
			
			// logic
			switch (field) {
			case NAME:
				includeThis &= checkName(tokenValue, job);
				break;
			case TYPE:
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getType(), tokenValue);
				break;
			case USER:
				includeThis &= job.isUserSurrogated() ? StringUtils.containsIgnoreCase(job.getJcl().getUser(), tokenValue) : StringUtils.containsIgnoreCase(job.getUser(), tokenValue);
				break;
			case ENVIRONMENT:
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getEnvironment(), tokenValue);
				break;
			case DOMAIN:
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getDomain(), tokenValue);
				break;
			case AFFINITY:
				includeThis &= StringUtils.containsIgnoreCase(job.getJcl().getAffinity(), tokenValue);
				break;
			case SUBMITTED_TIME:
				includeThis &= checkTime(tokenValue, job.getSubmittedTime());
				break;
			case PRIORITY:
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(job.getJcl().getPriority()), tokenValue);
				break;
			case MEMORY:
				includeThis &= StringUtils.containsIgnoreCase(String.valueOf(job.getJcl().getMemory()), tokenValue);
				break;
			case STEP:
				includeThis &= StringUtils.containsIgnoreCase(job.getCurrentStep().getName(), tokenValue);
				break;
			case RUNNING_TIME:
				includeThis &= checkTime(tokenValue, job.getStartedTime());
				break;
			case MEMBER:
				includeThis &= StringUtils.containsIgnoreCase(job.getMemberLabel(), tokenValue);
				break;
			case ENDED_TIME:
				includeThis &= checkTime(tokenValue, job.getEndedTime());
				break;
			case RETURN_CODE:
				includeThis &= checkReturnCode(tokenValue, job);
				break;
			case ID:
				includeThis &= StringUtils.containsIgnoreCase(job.getId(), tokenValue);
				break;
			default:
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
		if ("*".equalsIgnoreCase(tokenValue)) {
			return true;
		} else {
			if (tokenValue.endsWith("*")){
				String newTokenValue = StringUtils.substringBeforeLast(tokenValue, "*");
				return StringUtils.containsIgnoreCase(job.getName(), newTokenValue);
			} else {
				// testif a job id has been inserted
				MessageFormat jobIdFormat = new MessageFormat(Factory.JOBID_FORMAT);
				// checks if is by job id
				try {
					jobIdFormat.parse(tokenValue);
					return StringUtils.containsIgnoreCase(job.getId(), tokenValue);
				} catch (ParseException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
					return StringUtils.containsIgnoreCase(job.getName(), tokenValue);
				}
			}
		}		
	}
	
	/**
	 * Checks date of job
	 * @param time date of job
	 * @param tokenValue filter to check
	 * @return true if matches
	 */
	private boolean checkTime(String tokenValue, Date time){
		long now = System.currentTimeMillis();
		try {
			// parse the date value based on pattern
			long inputTime = TimeUtils.parseDuration(tokenValue);
			long jobTime = now-time.getTime();
			return jobTime <= inputTime;
		} catch (Exception e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// cannot parse the date, exclude this entry by default!
			return false;
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
			int inputReturnCode = Integer.parseInt(tokenValue);
			return job.getResult().getReturnCode() == inputReturnCode;
		} catch (Exception e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
			return false;
		}
	}
}