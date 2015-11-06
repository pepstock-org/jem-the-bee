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
package org.pepstock.jem.node.persistence.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.util.TimeUtils;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterParseException;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.JemFilterFields;
import org.pepstock.jem.util.filters.fields.JobFilterFields;

/**
 * Manages all SQL statements towards the database to persist the jobs in OUTPUT queue.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class OutputDBManager extends JobDBManager implements EvictionHandler<Job>{
	
	private final static String AND = " and ";

	/**
	 * Creates DB manager
	 * @param factory SQL factory
	 */
	public OutputDBManager(SQLContainerFactory factory) {
		super(Queues.OUTPUT_QUEUE, factory.getSQLContainerForOutputQueue(), true);
		super.setEnvictionHandler(this);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.EvictionHandler#fillSQLStatement(java.sql.PreparedStatement, java.lang.Object)
	 */
	@Override
	public void fillSQLStatement(PreparedStatement statement, Job item) throws SQLException {
		statement.setString(3, item.getName().toLowerCase());
		statement.setString(4, (item.isUserSurrogated()) ? item.getJcl().getUser().toLowerCase() : item.getUser().toLowerCase());
		if (item.getRoutingInfo() != null){
			statement.setBoolean(5, item.getRoutingInfo().getRoutedTime() != null);
		} else {
			statement.setBoolean(5, false);
		}
			
		statement.setLong(6, item.getSubmittedTime().getTime());
		if (item.getStartedTime() != null){
			statement.setLong(7, item.getStartedTime().getTime());
		} else {
			statement.setLong(7, item.getSubmittedTime().getTime());
		}
		statement.setLong(8, item.getEndedTime().getTime());
		statement.setInt(9, item.getResult().getReturnCode());
		statement.setString(10, item.getMemberLabel() != null ? item.getMemberLabel().toLowerCase() : null);
		if (item.getCurrentStep() != null){
			statement.setString(11, item.getCurrentStep().getName().toLowerCase());
		} else {
			statement.setString(11, null);	
		}
		statement.setString(12, item.getJcl().getType() != null ? item.getJcl().getType().toLowerCase() : null);
		statement.setString(13, item.getJcl().getEnvironment().toLowerCase());
		statement.setString(14, item.getJcl().getDomain().toLowerCase());
		statement.setString(15, item.getJcl().getAffinity().toLowerCase());
		statement.setInt(16, item.getJcl().getPriority());
		statement.setInt(17, item.getJcl().getMemory());
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.sql.AbstractDBManager#getStatementForFilter(org.pepstock.jem.util.filters.Filter)
	 */
	@Override
	String getStatementForFilter(Filter filter) {
		StringBuffer sb = new StringBuffer("select JOB from OUTPUT_QUEUE ");
		if (!filter.isEmpty()){
			sb.append("where ");
			// iterate over all filter tokens
			Iterator<FilterToken> iterator = filter.values().iterator();
			// exit if tokens already processed OR if i can immediate exclude this
			while(iterator.hasNext()) {
				FilterToken token = iterator.next();
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
						checkName(sb, token);
						break;
					case TYPE:
						// checks type of JOB
						addStringFilter(sb, token, field);
						break;
					case USER:
						// checks user (the surrogated as weel) of JOB
						addStringFilter(sb, token, field);
						break;
					case ENVIRONMENT:
						// checks environment of JOB
						addStringFilter(sb, token, field);
						break;
					case DOMAIN:
						// checks domain of JOB
						addStringFilter(sb, token, field);
						break;
					case AFFINITY:
						// checks affinity of JOB
						addStringFilter(sb, token, field);
						break;
					case SUBMITTED_TIME:
						// checks the submitted time of JOB
						addTimeFilter(sb, token, field);
						break;
					case PRIORITY:
						// checks the priority of JOB
						addNumberFilter(sb, token, field);
						break;
					case MEMORY:
						// checks the memory requested of JOB
						addNumberFilter(sb, token, field);
						break;
					case STEP:
						// checks the current step of JOB
						addStringFilter(sb, token, field);
						break;
					case RUNNING_TIME:
						// checks the running time of JOB
						addTimeFilter(sb, token, field);
						break;
					case MEMBER:
						// checks the JEM node where the job is executing
						addStringFilter(sb, token, field);
						break;
					case ENDED_TIME:
						// checks the ended time of JOB
						addTimeFilter(sb, token, field);
						break;
					case RETURN_CODE:
						// checks the return code of JOB
						addNumberFilter(sb, token, field);
						break;
					case ID:
						// checks the ID of JOB
						addStringFilter(sb, token, field);
						break;
						// checks JOB is routed
					case ROUTED:
						boolean wantRouted = tokenValue.trim().equalsIgnoreCase(JemFilterFields.YES);
						sb.append(field.getSqlField()).append(" ").append((token.isNot() ? "< " : ">= ")).append((wantRouted ? 1 : 0)).append(AND);
						break;
					default:
						// otherwise it uses a wrong filter name
						throw new JemRuntimeException("Unrecognized Job filter field: " + field);
				}
			}
		}
		return StringUtils.substringBeforeLast(sb.toString(), AND);
	}

	private void addTimeFilter(StringBuffer statement, FilterToken token, JobFilterFields field){
		try {
			if (token.getValue() != null){
				// gets current time
				// used to subtract the filter value
				long now = System.currentTimeMillis();
				long inputTime = now - TimeUtils.parseDuration(token.getValue());
				statement.append(field.getSqlField()).append(" ").append((token.isNot() ? "< " : ">= ")).append(inputTime).append(AND);
			}
		} catch (FilterParseException e) {
			// ignore
			LogAppl.getInstance().ignore(e.getMessage(), e);
		}
	}
	
	private void addNumberFilter(StringBuffer statement, FilterToken token, JobFilterFields field){
		if (token.getValue() != null && StringUtils.isNumeric(token.getValue())){
			statement.append(field.getSqlField()).append(" ").append((token.isNot() ? "!= " : "= ")).append(token.getValue()).append(AND);
		}
	}
	
	private void addStringFilter(StringBuffer statement, FilterToken token, JobFilterFields field){
		addStringFilter(statement, token, field, token.getValue());
	}
	
	private void addStringFilter(StringBuffer statement, FilterToken token, JobFilterFields field, String newTokenvalue){
		if (token.getValue() != null){
			statement.append(field.getSqlField()).append(" ").append((token.isNot() ? "NOT LIKE " : "LIKE ")).append("'%").append(newTokenvalue.toLowerCase()).append("%' ").append(AND);
		}
	}
	
	/**
	 * Checks the filter name
	 * @param tokenValue filter passed
	 * @param job job instance
	 */
	private void checkName(StringBuffer statement, FilterToken token){
		// is able to manage for job name the * wildcard
		// matches ALWAYS if has got the star only
		if ("*".equalsIgnoreCase(token.getValue()) || token.getValue() == null) {
			return;
		} else {
			// checks if ends with wildcard
			if (token.getValue().endsWith("*")){
				// if yes, remove the stars
				String newTokenValue = StringUtils.substringBeforeLast(token.getValue(), "*");
				// and compares if the value is in the job name
				addStringFilter(statement, token, JobFilterFields.NAME, newTokenValue);
			} else {
				// testif a job id has been inserted
				MessageFormat jobIdFormat = new MessageFormat(Factory.JOBID_FORMAT);
				// checks if is by job id
				try {
					// try to parse the job id
					jobIdFormat.parse(token.getValue());
					// checks if the ID is the same
					addStringFilter(statement, token, JobFilterFields.ID);
				} catch (ParseException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// if here means that is not a JOB ID
					// then it uses the job name
					addStringFilter(statement, token, JobFilterFields.NAME);
				}
			}
		}		
	}
}