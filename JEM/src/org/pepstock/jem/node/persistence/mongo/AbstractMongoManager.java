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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.persistence.AbstractDatabaseManager;
import org.pepstock.jem.node.persistence.DatabaseException;
import org.pepstock.jem.node.persistence.DatabaseManager;
import org.pepstock.jem.util.filters.Filter;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;

/**
 * Manages all statements towards the MONGO database to persist the Hazelcast items.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 * @param <T> object stored in Hazelcast map
 */
public abstract class AbstractMongoManager<T> extends AbstractDatabaseManager<T> implements DatabaseManager<T> {
	
	static final String MONGO_KEY_FOR_INDEX = "key";

	private String queueName = null;
	
	private String fieldKey = null;
	
	private MongoCollection<Document> collection = null;

	/**
	 * Creates a MONGO manager using HC queue/map name and the field name used as KEY in the 
	 * Mongo collection.
	 * @param queueName HC queue/map name
	 * @param fieldKey the field name used as KEY in the Mongo collection
	 */
	AbstractMongoManager(String queueName, String fieldKey) {
		this(queueName, fieldKey, false);
	}
	
	/**
	 * Creates a MONGO manager using HC queue/map name and the field name used as KEY in the 
	 * Mongo collection.
	 * @param queueName HC queue/map name
	 * @param fieldKey the field name used as KEY in the Mongo collection
	 * @param canBeEvicted if the map can be evicted
	 */
	AbstractMongoManager(String queueName, String fieldKey, boolean canBeEvicted) {
		super(canBeEvicted);
		this.queueName = queueName;
		this.fieldKey = fieldKey;
		// saves collection
		this.collection = DBManager.getInstance().getCollection(queueName);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#getQueueName()
	 */
	@Override
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @return the collection
	 */
	MongoCollection<Document> getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(MongoCollection<Document> collection) {
		this.collection = collection;
	}

	/**
	 * Creates the object starting from JSON
	 * @param mapper JSON mapper to use to deserialize
	 * @param objFound JSON representation of the object
	 * @return object to add to HC
	 * @throws IOException if any error occurs
	 */
	public abstract T createObject(ObjectMapper mapper, String objFound) throws IOException;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#delete(java.lang.String)
	 */
	@Override
	public void delete(String key) throws DatabaseException {
		try {
			// creates the filter for delete
			BasicDBObject query = new BasicDBObject(fieldKey, key);
			// performs query
			FindIterable<Document> cursor = collection.find(query);
			Document first = cursor.first();
			// if exists
			if (first != null) {
				// deletes the document
				collection.deleteOne(first);
			}
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#insert(java.lang.Object)
	 */
	@Override
	public void insert(T item) throws DatabaseException {
		// gets the key
		String key = getKey(item);
		try {
			// creates filter
			BasicDBObject query = new BasicDBObject(fieldKey, key);
			// performs query
			FindIterable<Document> cursor= collection.find(query);
			Document first = cursor.first();
			// if not exists
			if (first == null) {
				// transforms in JSON and BSON
				String json = JsonMapper.getInstance().getMapper().writeValueAsString(item);
				Document obj = Document.parse(JSON.parse(json).toString());
				// inserts into mongo
				collection.insertOne(obj);
			} else {
				// if exists update the document
				update(item);
			}
		} catch (Exception e) {
			throw new DatabaseException(e); 
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#update(java.lang.Object)
	 */
	@Override
	public void update(T item) throws DatabaseException {
		// gets the key
		String key = getKey(item);
		try {
			// creates filter
			BasicDBObject query = new BasicDBObject(fieldKey, key);
			// performs query
			FindIterable<Document> cursor= collection.find(query);
			Document first = cursor.first();
			// if exists
			if (first != null) {
				// transforms in JSON and BSON
				String json = JsonMapper.getInstance().getMapper().writeValueAsString(item);
				Document obj = Document.parse(JSON.parse(json).toString());
				// updates into mongo
				collection.replaceOne(query, obj);
			} else {
				// if not exists insert the document
				insert(item);
			}
		} catch (Exception e) {
			throw new DatabaseException(e); 
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#getAllKeys()
	 */
	@Override
	public Set<String> getAllKeys() throws DatabaseException {
		MongoCursor<Document> cursor = null;
		// creates the object to be returned 
		Set<String> keys = new HashSet<String>();
		try {
			// performs query
			FindIterable<Document> iterator = collection.find();
			Document first = iterator.first();
			// if there is a result
			if (first != null){
				cursor = iterator.iterator();
				while (cursor.hasNext()) {
					// scans documents
					Document doc = cursor.next();
					String myKey = (String)doc.get(fieldKey);
					// adds key
					if (myKey != null){
						keys.add(myKey);
					}
				}
			}
		} catch (Exception e) {
			throw new DatabaseException(e);
		} finally {
			// if cursor exists
			if (cursor != null){
				try {
					// closes the cursor
					cursor.close();
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return keys;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#getAllItems()
	 */
	@Override
	public Map<String, T> getAllItems() throws DatabaseException {
		MongoCursor<Document> cursor = null;
		// creates the object to be returned 
		Map<String, T> items = new HashMap<String, T>();
		try {
			// performs query
			FindIterable<Document> iterator = collection.find();
			Document first = iterator.first();
			// if there is a result
			if (first != null){
				cursor = iterator.iterator();
				while (cursor.hasNext()) {
					// scans documents
					Document doc = cursor.next();
					// creates the object from BSON and JSON
					T item = createObject(JsonMapper.getInstance().getMapper(), JSON.parse(doc.toJson()).toString());
					String key = getKey(item);
					// adds object to map
					items.put(key, item);
				}
			}
		} catch (Exception e) {
			throw new DatabaseException(e);
		} finally {
			// if cursor exists
			if (cursor != null){
				try {
					// closes the cursor
					cursor.close();
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#getAllItems(java.util.Collection)
	 */
	@Override
	public Map<String, T> getAllItems(Collection<String> keys) throws DatabaseException {
		MongoCursor<Document> cursor = null;
		// creates the object to be returned 
		Map<String, T> items = new HashMap<String, T>();
		// if keys are not 
		if (!keys.isEmpty()){
			try {
				// creates the objects for query
				BasicDBObject inQuery = new BasicDBObject();
				inQuery.put(fieldKey, new BasicDBObject(Operator.IN_LIST.getName(), keys));
				// performs query
				FindIterable<Document> iterator = collection.find(inQuery);
				Document first = iterator.first();
				if (first != null){
					cursor = iterator.iterator();
					while (cursor.hasNext()) {
						// scans documents
						Document doc = cursor.next();
						// creates the object from BSON and JSON
						T item = createObject(JsonMapper.getInstance().getMapper(), JSON.parse(doc.toJson()).toString());
						String key = getKey(item);
						// adds object to map
						items.put(key, item);
					}

				}
			} catch (Exception e) {
				throw new DatabaseException(e);
			} finally {
				// if cursor exists
				if (cursor != null){
					try {
						// closes the cursor
						cursor.close();
					} catch (Exception e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#getItem(java.lang.String)
	 */
	@Override
	public T getItem(String key) throws DatabaseException {
		try {
			// creates the query object
			BasicDBObject query = new BasicDBObject(fieldKey, key);
			// performs query
			FindIterable<Document> cursor= collection.find(query);
			Document first = cursor.first();
			if (first != null) {
				// creates the object from BSON and JSON
				return createObject(JsonMapper.getInstance().getMapper(), JSON.parse(first.toJson()).toString());
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new DatabaseException(e); 
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#getSize()
	 */
	@Override
	public long getSize() throws DatabaseException {
		long size= 0;
		MongoCursor<Document> cursor = null;
		try {
			// scans all documents
			FindIterable<Document> iterator = collection.find();
			Document first = iterator.first();
			if (first != null){
				cursor = iterator.iterator();
				while (cursor.hasNext()) {
					// scans documents and
					// use the size of JSON to calculate the size
					size += cursor.next().toJson().length();
				}
			}
		} catch (Exception e) {
			throw new DatabaseException(e);
		} finally {
			// if cursor exists
			if (cursor != null){
				try {
					// closes the cursor
					cursor.close();
				} catch (Exception e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		return size;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#checkAndCreate()
	 */
	@Override
	public void checkAndCreate() throws DatabaseException {
		// creates a BSON document 
		// which represents the structure of index
		// based on teh KEY of the object
		Document indexFormat = new Document(fieldKey, 1);
		try {
			// gets all indexes
			ListIndexesIterable<Document> listIndexes = collection.listIndexes();
			Iterator<Document> iter = listIndexes.iterator();
			// scans all documents
			if (iter != null){
				while(iter.hasNext()){
					Document index = iter.next();
					// the key document which
					// represents the index structure
					Document key = (Document)index.get(MONGO_KEY_FOR_INDEX);
					// if already exists the necessary index
					// returns
					if (key != null && key.equals(indexFormat)){
						return;
					}
				}
			}
			// if here
			// the needed index is not present
			// therefore it creates
			collection.createIndex(indexFormat);
		} catch (MongoTimeoutException e) {
			throw new DatabaseException(e);
		}
	}
	
	/**
	 * Called for managers which can provide a query on the documents 
	 * @param filter filter to apply
	 * @return Document to use as filter on MONGO
	 */
	Document getMongoFilter(Filter filter){
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DatabaseManager#loadAll(org.pepstock.jem.util.filters.Filter)
	 */
	@Override
	public Collection<T> loadByFilter(Filter filter) throws DatabaseException {
		MongoCursor<Document> cursor = null;
		// creates the object to return
		Collection<T> items = new ArrayList<T>();
		// gets the filter
		Document mongoFilter = getMongoFilter(filter);
		// if not null
		if (mongoFilter != null){
			try {
				// find on Mongodb 
				FindIterable<Document> iterator = mongoFilter.isEmpty() ? collection.find() : collection.find(mongoFilter);
				// checks the result
				Document first = iterator.first();
				if (first != null){
					cursor = iterator.iterator();
					while (cursor.hasNext()) {
						// scans the iterator
						Document doc = cursor.next();
						// creates the objects
						T item = createObject(JsonMapper.getInstance().getMapper(), JSON.parse(doc.toJson()).toString());
						// adds to list
						items.add(item);
					}
				}
			} catch (Exception e) {
				throw new DatabaseException(e);
			} finally {
				// if cursor is not null
				if (cursor != null){
					try {
						// closes the cursor
						cursor.close();
					} catch (Exception e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}
		}
		return items;
	}	
}
