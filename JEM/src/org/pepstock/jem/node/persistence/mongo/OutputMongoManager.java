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
package org.pepstock.jem.node.persistence.mongo;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.JemRuntimeException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.persistence.DatabaseException;
import org.pepstock.jem.util.Numbers;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.TimeUtils;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterParseException;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.JemFilterFields;
import org.pepstock.jem.util.filters.fields.JobFilterFields;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.ListIndexesIterable;

/**
 * Map manager based on MONGO for jobs into OUTPUT queue.
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class OutputMongoManager extends JobMongoManager {
	
	private static final String NAME_FIELD = "name";

	/**
	 * Creates the object setting queue 
	 */
	public OutputMongoManager() {
		super(Queues.OUTPUT_QUEUE, true);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#checkAndCreate()
	 */
	@Override
	public void checkAndCreate() throws DatabaseException {
		// calls the super to create the USUAL structure
		super.checkAndCreate();
		// creates a new index base on name field of document
		Document indexFormat = new Document(NAME_FIELD, 1);
		try {
			// gets all indexes
			ListIndexesIterable<Document> listIndexes = super.getCollection().listIndexes();
			Iterator<Document> iter = listIndexes.iterator();
			// iterates all indexes
			if (iter != null){
				while(iter.hasNext()){
					Document index = iter.next();
					// gets the key of index
					Document key = (Document)index.get(MONGO_KEY_FOR_INDEX);
					// if equals to new index
					// it doesn't have to create any index
					// returns
					if (key != null && key.equals(indexFormat)){
						return;
					}
				}
			}
			// creates the index
			super.getCollection().createIndex(indexFormat);
		} catch (MongoTimeoutException e) {
			throw new DatabaseException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.mongo.AbstractMongoManager#getMongoFilter(org.pepstock.jem.util.filters.Filter)
	 */
	@Override
	Document getMongoFilter(Filter filter) {
		Document mongoDocument = new Document();
		if (!filter.isEmpty()){
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
						checkName(mongoDocument, token);
						break;
					case TYPE:
						// checks type of JOB
						addStringFilter(mongoDocument, token, field);
						break;
					case USER:
						// checks user (the surrogated as weel) of JOB
						addUserFilter(mongoDocument, token, field);
						break;
					case ENVIRONMENT:
						// checks environment of JOB
						addStringFilter(mongoDocument, token, field);
						break;
					case DOMAIN:
						// checks domain of JOB
						addStringFilter(mongoDocument, token, field);
						break;
					case AFFINITY:
						// checks affinity of JOB
						addStringFilter(mongoDocument, token, field);
						break;
					case SUBMITTED_TIME:
						// checks the submitted time of JOB
						addTimeFilter(mongoDocument, token, field);
						break;
					case PRIORITY:
						// checks the priority of JOB
						addNumberFilter(mongoDocument, token, field);
						break;
					case MEMORY:
						// checks the memory requested of JOB
						addNumberFilter(mongoDocument, token, field);
						break;
					case STEP:
						// checks the current step of JOB
						addStringFilter(mongoDocument, token, field);
						break;
					case RUNNING_TIME:
						// checks the running time of JOB
						addTimeFilter(mongoDocument, token, field);
						break;
					case MEMBER:
						// checks the JEM node where the job is executing
						addStringFilter(mongoDocument, token, field);
						break;
					case ENDED_TIME:
						// checks the ended time of JOB
						addTimeFilter(mongoDocument, token, field);
						break;
					case RETURN_CODE:
						// checks the return code of JOB
						addNumberFilter(mongoDocument, token, field);
						break;
					case ID:
						// checks the ID of JOB
						addStringFilter(mongoDocument, token, field);
						break;
						// checks JOB is routed
					case ROUTED:
						boolean wantRouted = token.isNot() ? tokenValue.trim().equalsIgnoreCase(JemFilterFields.NO) : tokenValue.trim().equalsIgnoreCase(JemFilterFields.YES);
						mongoDocument.put(field.getMongoField(), new Document(Operator.EXISTS.getName(), wantRouted));
						break;
					default:
						// otherwise it uses a wrong filter name
						throw new JemRuntimeException("Unrecognized Job filter field: " + field);
				}
			}
		}
		return mongoDocument;
	}

	/**
	 * Adds the "user" filter into filter document
	 * @param doc main filter document
	 * @param token token to apply
	 * @param field job filter description
	 */
	private void addUserFilter(Document doc, FilterToken token, JobFilterFields field){
		// checks if value is not null
		if (token.getValue() != null){
			// the user field can be into job or jcl
			String[] fields = StringUtils.split(field.getMongoField(), ",");
			// if only one, use the standard string filter
			if (fields.length == Numbers.N_1){
				addStringFilter(doc, token, field);
			} else if (fields.length == Numbers.N_2){
				// if here it ahs to search into job and jcl
				List<Document> list = new ArrayList<Document>();
				// creates document for job
				Document subDoc = new Document(); 
				// creates document for jcl
				Document subDoc1 = new Document();
				if (token.isNot()){
					// adds the filter with a regex in NOT
					subDoc.put(fields[Numbers.N_0].trim(), new Document(Operator.REG_EX.getName(), "^((?!("+token.getValue()+")).)*$").append("$options", "i"));
					subDoc1.put(fields[Numbers.N_1].trim(), new Document(Operator.REG_EX.getName(), "^((?!("+token.getValue()+")).)*$").append("$options", "i"));
				} else {
					// adds the filter with a regex
					subDoc.put(fields[Numbers.N_0].trim(), new Document(Operator.REG_EX.getName(), ".*"+token.getValue()+".*").append("$options", "i"));
					subDoc1.put(fields[Numbers.N_1].trim(), new Document(Operator.REG_EX.getName(), ".*"+token.getValue()+".*").append("$options", "i"));
				}
				// adds document in the list
				list.add(subDoc);
				list.add(subDoc1);
				// adds to main filter document in OR
				doc.put(Operator.OR.getName(), list);
			}
		}
	}
	
	/**
	 * Adds a filter based on time 
	 * @param doc main filter document
	 * @param token token to apply
	 * @param field job filter description
	 */
	private void addTimeFilter(Document doc, FilterToken token, JobFilterFields field){
		if (token.getValue() != null){
			try {
				// gets current time
				// used to subtract the filter value
				long now = System.currentTimeMillis();
				// checks input ime based on filter
				long inputTime = now - TimeUtils.parseDuration(token.getValue());
				// adds filter
				doc.put(field.getMongoField(), new Document(token.isNot() ? Operator.LESS_THAN.getName() : Operator.GREATER_OR_EQUALS_THAN.getName(), inputTime));
			} catch (FilterParseException e) {
				// ignore
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
	}

	/**
	 * Adds a filter based on integer
	 * @param doc main filter document
	 * @param token token to apply
	 * @param field job filter description
	 */
	private void addNumberFilter(Document doc,  FilterToken token, JobFilterFields field){
		// if not null and is a number
		if (token.getValue() != null && StringUtils.isNumeric(token.getValue())){
			// adds filter
			doc.put(field.getMongoField(), new Document(token.isNot() ? Operator.NOT_EQUALS.getName() : Operator.EQUALS.getName(), Parser.parseInt(token.getValue())));
		}
	}

	/**
	 * Adds a filter based on string which must be contained in a field value 
	 * @param doc main filter document
	 * @param token token to apply
	 * @param field job filter description
	 */
	private void addStringFilter(Document doc, FilterToken token, JobFilterFields field){
		addStringFilter(doc, token, field, token.getValue());
	}

	/**
	 * Adds a filter based on string which must be contained in a field value 
	 * @param doc main filter document
	 * @param token token to apply
	 * @param field job filter description
	 * @param newTokenvalue changed token value
	 */
	private void addStringFilter(Document doc, FilterToken token, JobFilterFields field, String newTokenvalue){
		// if not null 
		if (token.getValue() != null){
			if (token.isNot()){
				// adds the filter with a regex in NOT
				doc.put(field.getMongoField(), new Document(Operator.REG_EX.getName(), "^((?!("+newTokenvalue+")).)*$").append("$options", "i"));
			} else {
				// adds filter using regex
				doc.put(field.getMongoField(), new Document(Operator.REG_EX.getName(), ".*"+newTokenvalue+".*").append("$options", "i"));
			}

		}
	}

	/**
	 * Checks the job name filter
	 * @param doc main filter document
	 * @param token token to apply
	 */
	private void checkName(Document doc,FilterToken token){
		// is able to manage for job name the * wild-card
		// matches ALWAYS if has got the star only
		if ("*".equalsIgnoreCase(token.getValue()) || token.getValue() == null) {
			return;
		} else {
			// checks if ends with wild-card
			if (token.getValue().endsWith("*")){
				// if yes, remove the stars
				String newTokenValue = StringUtils.substringBeforeLast(token.getValue(), "*");
				// and compares if the value is in the job name
				addStringFilter(doc, token, JobFilterFields.NAME, newTokenValue);
			} else {
				// test if a job id has been inserted
				MessageFormat jobIdFormat = new MessageFormat(Factory.JOBID_FORMAT);
				// checks if is by job id
				try {
					// try to parse the job id
					jobIdFormat.parse(token.getValue());
					// checks if the ID is the same
					addStringFilter(doc, token, JobFilterFields.ID);
				} catch (ParseException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
					// if here means that is not a JOB ID
					// then it uses the job name
					addStringFilter(doc, token, JobFilterFields.NAME);
				}
			}
		}		
	}
}
