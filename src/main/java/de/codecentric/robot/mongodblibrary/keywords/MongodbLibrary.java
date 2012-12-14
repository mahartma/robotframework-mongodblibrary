package de.codecentric.robot.mongodblibrary.keywords;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import de.codecentric.robot.mongodblibrary.MongodbLibraryException;

/**
 * This library supports mongodb-related testing using the Robot Framework.
 */
public class MongodbLibrary {

	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
	
	private MongoClient mongoClient;
	private DB db;

	/**
	 * Connecting to a local mongodb instance.
	 */
	public MongodbLibrary() {
		this("localhost", 27017, "robotdb");
		
	}
	
	/**
	 * Connecting to a specific mongodb server.
	 */
	public MongodbLibrary(String server) {
		this(server, 27017, "robotdb");
	}
	
	/**
	 * Connecting to a specific mongodb server with given port.
	 */
	public MongodbLibrary(String server, int port) {
		this(server, port, "robotdb");
	}

	/**
	 * Connecting to a specific mongodb server with given port and database.
	 */
	public MongodbLibrary(String server, int port, String database) {
		try {
			mongoClient = new MongoClient(server , port);
			db = mongoClient.getDB(database);
		} catch (UnknownHostException e) {
			throw new MongodbLibraryException("error connecting mongodb", e);
		}
	}
	
	/**
	 *  Inserts the given Json-Document into the given collection. 
	 * 
	 * Arguments:
	 * - _collectionName_: the name of the target collection
	 * - _jsonString_: the json document to persist
	 *  
	 * Example:
	 * | Insert Json Document Into Collection | myCollection | {say : 'Hello MongoDB!'} |
	 */
	public void insertJsonDocumentIntoCollection(String collectionName, String jsonString) {
		db.getCollection(collectionName).insert((DBObject) JSON.parse(jsonString));
	}

	/**
	 *  Drops the given collection.
	 *  
	 * Arguments:
	 * - _collectionName_: the name of the collection to drop
	 *   
	 * Example:
	 * | Drop Collection | myCollection |
	 */
	public void dropCollection(String collectionName) {
		db.getCollection(collectionName).drop();
	}
	
}
