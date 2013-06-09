package de.codecentric.robot.mongodblibrary.keywords;

import static com.mongodb.util.JSON.parse;
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6;
import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import de.codecentric.robot.mongodblibrary.MongodbLibraryException;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.process.distribution.GenericVersion;

/**
 * This library supports mongodb-related testing using the Robot Framework.
 */
public class MongodbLibrary {

	public static final String VERSION = ResourceBundle.
			getBundle(MongodbLibrary.class.getName()).getString("lib.version");
	
	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

	private static final int MONGO_DEFAULT_PORT = 27020;

	private MongoClient mongoClient;
	private DB db;
	private MongodExecutable mongodExecutable;

	/**
	 * starts a MongoDB-Server in the given version
	 * 
	 * Arguments: 
	 * - _version_: MongoDB-Version
	 * 
	 * Example: 
	 * | Startup Embedded | 2.4.1 |
	 */
	public void startupEmbedded(String version) throws IOException {
		MongodConfig mongodConfig = new MongodConfig(new GenericVersion(version), MONGO_DEFAULT_PORT, localhostIsIPv6());
		MongodStarter runtime = MongodStarter.getDefaultInstance();
		mongodExecutable = runtime.prepare(mongodConfig);
		mongodExecutable.start();
	}

	/**
	 * starts a MongoDB-Server in the given version on the given port
	 * 
	 * Arguments: 
	 * - _version_: MongoDB-Version
	 * - _port_: port to use
	 * 
	 * Example: 
	 * | Startup Embedded | 2.4.1 | 27042 |
	 */
	public void startupEmbeddedOnPort(String version, String port) throws IOException {
		MongodConfig mongodConfig = new MongodConfig(new GenericVersion(version), parseInt(port), localhostIsIPv6());
		MongodStarter runtime = MongodStarter.getDefaultInstance();
		mongodExecutable = runtime.prepare(mongodConfig);
		mongodExecutable.start();
	}
	
	/**
	 * stops the previously started MongoDB-Server (counter-part to the keywords: `Startup Embedded` and `Startup Embedded On Port`)
	 * 
	 * Example: 
	 * | Shutdown Embedded |
	 */
	public void shutdownEmbedded() {
		if (mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}
	
	/**
	 * connects to the given MongoDB-Server
	 * 
	 * Arguments: 
	 * - _server_: server to connect
	 * - _port_: port to connect
	 * - _database_: database to connect
	 * 
	 * Example: 
	 * | Connect To Server | localhost | 27017 | robotdb |
	 */
	public void connectToServer(String server, String port, String database) {
		try {
			mongoClient = new MongoClient(server, parseInt(port));
			db = mongoClient.getDB(database);
		} catch (UnknownHostException e) {
			throw new MongodbLibraryException("error connecting mongodb", e);
		}
	}

	/**
	 * Inserts the given document into the given collection.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of the target collection 
	 * - _jsonString_: the document to persist as JSON
	 * 
	 * Example: 
	 * | Insert Document | myCollection | {say : 'Hello MongoDB!'} |
	 */
	public void insertDocument(String collectionName,
			String jsonString) {
		db.getCollection(collectionName).insert(
				(DBObject) parse(jsonString));
	}

	/**
	 * Updates documents in the given collection.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of the collection 
	 * - _queryJsonString_: the documents to update as JSON
	 * - _newObjectJsonString_: the updated document as JSON
	 * 
	 * Example: 
	 * | Update Documents | myCollection | { age : { $gte: 23 } } | {name : 'Mike', age : 22} | 
	 */
	public void updateDocuments(String collectionName,
			String queryJsonString, String newObjectJsonString) {
		DBObject queryJson = (DBObject) parse(queryJsonString);
		DBObject newObjectJson = (DBObject) parse(newObjectJsonString);
		db.getCollection(collectionName).update(queryJson, newObjectJson, false, true);
	}
	
	/**
	 * Imports the documents from the given file into the given collection.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of the target collection
	 * - _file_: the file that contains the documents
	 * 
	 * Example:
	 * | Import Documents | myCol | /data/documents.json |
	 */
	public void importDocuments(String collectionName, String file) {
		try {
			String documents = IOUtils.toString(new FileReader(file));
			DBObject dbObject = (DBObject) parse(documents);
			if (dbObject instanceof BasicDBList) {
				Iterator<Object> jsonIterator = ((BasicDBList) dbObject).iterator();
				while(jsonIterator.hasNext()) {
					DBObject document = (DBObject) jsonIterator.next();
					db.getCollection(collectionName).insert(document);
				}
			} else {
				db.getCollection(collectionName).insert(dbObject);
			}
			
		} catch (IOException e) {
			throw new MongodbLibraryException("error reading json-file", e);
		}
	}

	/**
	 * Imports the documents from the given file into the given collection. This keyword reads the data row-based.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of the target collection
	 * - _file_: the file that contains the documents
	 * 
	 * Example:
	 * | Import Documents Row Seperated | myCol | /data/documents.json |
	 */
	public void importDocumentsRowSeperated(String collectionName, String file) {
		try {
			String documents = IOUtils.toString(new FileReader(file));
			String[] documentsAsArray = documents.split("\n");
			for (String json : documentsAsArray) {
				db.getCollection(collectionName).insert((DBObject) JSON.parse(json));
			}
			
		} catch (IOException e) {
			throw new MongodbLibraryException("error reading json-file", e);
		}
	}


	/**
	 * Drops the given collection.
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

	/**
	 * All following keywords will operate on the selected database. Same as use
	 * dbname with the Mongo shell. If the database does not exist, it will be
	 * create as soon as needed.
	 * 
	 * Arguments: 
	 * - _databaseName_: the name of the database
	 * 
	 * Example: 
	 * | Use Database | myDb |
	 */
	public void useDatabase(String databaseName) {
		this.db = mongoClient.getDB(databaseName);
	}

	/**
	 * A database in MongoDB is a more lightweight construct compared to RDBMS,
	 * so a cleanup after testing can be done quite easily by dropping the whole database.
	 * 
	 * Arguments: 
	 * - _databaseName_: the name of the database to delete
	 * 
	 * Example: 
	 * | Drop Database | myDb |
	 */
	public void dropDatabase(String databaseName) {
		mongoClient.dropDatabase(databaseName);
	}
	
	/**
	 * Creates a collection with the given name and options.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of the collection to create
	 * - _options_: the options of the collection
	 * 
	 * Example: 
	 * | Create Collection | myCol | {capped:true, size:10000} |
	 */
	public void createCollectionWithOptions(String collectionName, String options) {
		this.db.createCollection(collectionName, (DBObject) JSON.parse(options));
	}
	
	/**
	 * Creates a collection with the given name and options.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of the collection to create
	 * 
	 * Example: 
	 * | Create Collection | myCol |
	 */
	public void createCollection(String collectionName) {
		this.db.createCollection(collectionName, new BasicDBObject());
	}
	
	/**
	 * Creates an index on the given collection, the desired fields of the index are given by the keys parameter.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of collection
	 * - _keys_: an object with a key set of the fields desired for the index
	 * 
	 * Example:
	 * | Ensure Index | myCol | {name : 1, street : 1} |
	 */
	public void ensureIndex(String collectionName, String keys) {
		this.db.getCollection(collectionName).ensureIndex((DBObject) JSON.parse(keys));
	}
	
	/**
	 * Creates an index with given name on the given collection, the desired fields of the index are given by the keys parameter.
	 * 
	 * Arguments:
	 * - _indexName_: the name of the index
	 * - _collectionName_: the name of collection
	 * - _keys_: an object with a key set of the fields desired for the index
	 * 
	 * Example:
	 * | Ensure Index | myCol | {name : 1, street : 1} |
	 */
	public void ensureIndexWithName(String indexName, String collectionName, String keys
			) {
		this.db.getCollection(collectionName).ensureIndex((DBObject) JSON.parse(keys), indexName);
	}
	
	/**
	 * Creates an unique index with given name on the given collection, the desired fields of the index are given by the keys parameter.
	 * 
	 * Arguments:
	 * - _indexName_: the name of the index
	 * - _collectionName_: the name of collection
	 * - _keys_: an object with a key set of the fields desired for the index
	 * 
	 * Example:
	 * | Ensure Index | myCol | {name : 1, street : 1} |
	 */
	public void ensureUniqueIndex(String collectionName, String keys,
			String indexName) {
		this.db.getCollection(collectionName).ensureIndex((DBObject) JSON.parse(keys), indexName, true);
	}
	
	/**
	 *  Fails if the database does not exist.
	 *  
	 *  Arguments:
	 *  - _databaseName_: the database which should exist
	 *  
	 *  Example:
	 *  | Database Should Exist | myDatabase |
	 */
	public void databaseShouldExist(String databaseName) {
		assertTrue("Database: " + databaseName + " does not exist.", this.mongoClient.getDatabaseNames().contains(databaseName));
	}
	
	/**
	 *  Fails if the collection does not exist.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the collection which should exist
	 *  
	 *  Example:
	 *  | Collection Should Exist | myCol |
	 */
	public void collectionShouldExist(String collectionName) {
		assertTrue("Collection: " + collectionName + " does not exist.", this.db.getCollectionNames().contains(collectionName));
	}
	
	/**
	 *  Fails if the given document does not exist in the given collection.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the collection within the document should exist
	 *  - _document_: the document which should exist in the given collection
	 *  
	 *  Example:
	 *  | Document Should Exist | myCol | {say : 'Hello MongoDb!'} |
	 */
	public void documentShouldExist(String collectionName, String document) {
		assertTrue("Document " + document + " does not exist in Collection " + collectionName + ".", this.db.getCollection(collectionName).find((DBObject) JSON.parse(document)).count() == 1);
	}

	/**
	 *  Fails if the given index does not exist in the given collection.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the collection within the index should exist
	 *  - _index_: the name of the index which should exist in the given collection
	 *  
	 *  Example:
	 *  | Index Should Exist | myCol | a_1_b_1 |
	 */
	public void indexShouldExist(String collectionName, String indexName) {
		for (DBObject index : this.db.getCollection(collectionName).getIndexInfo()) {
			if(index.get("name").equals(indexName)) {
				return;
			}
		}
		fail("Index " + indexName + " does not exist in Collection " + collectionName + ".");		
	}
	
	DB getDb() {
		return db;
	}

	/**
	 *  Returns all documents from the given collection.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the name of the collection
	 *  
	 *  Example:
	 *  | Get All Documents | myCol |
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllDocuments(String collectionName) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (DBObject document : db.getCollection(collectionName).find()) {
			ret.add(document.toMap());
		}
		return ret;
	}

	/**
	 *  Finds documents in the given collection.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the name of the collection
	 *  - _jsonString_: the documents to find as JSON
	 *  
	 *  Example:
	 *  | Get Documents | myCol | { age : { $gte: 23 } } |
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDocuments(String collectionName, String jsonString) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (DBObject document : db.getCollection(collectionName).find(
				(DBObject) parse(jsonString))) {
			ret.add(document.toMap());
		}
		return ret;
	}

	/**
	 *  Removes some documents in the given collection.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the name of the collection
	 *  - _jsonString_: the documents to remove as JSON
	 *  
	 *  Example:
	 *  | Remove Documents | myCol | { age : { $gte: 23 } } |
	 */	
	public void removeDocuments(String collectionName, String jsonString) {
		for (DBObject document : db.getCollection(collectionName).find(
				(DBObject) parse(jsonString))) {
			db.getCollection(collectionName).remove(document);
		}
	}

	/**
	 *  Removes all documents in the given collection.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the name of the collection
	 *  
	 *  Example:
	 *  | Remove All Documents | myCol |
	 */	
	public void removeAllDocuments(String collectionName) {
		for (DBObject document : db.getCollection(collectionName).find()) {
			db.getCollection(collectionName).remove(document);
		}
	}

	/**
	 *  Returns the number of documents in the given collection.
	 *  
	 *  Arguments:
	 *  - _collectionName_: the name of the collection
	 *  
	 *  Example:
	 *  | Get Collection Count | myCol |
	 */	
	public long getCollectionCount(String collectionName) {
		return db.getCollection(collectionName).count();
	}

	/**
	 *  Returns the names of the collections from the connected database.
	 *  
	 *  Example:
	 *  | Get Collections |
	 */	
	public List<String> getCollections() {
		Set<String> collectionNames = db.getCollectionNames();
		collectionNames.remove("system.indexes");
		return new ArrayList<String>(collectionNames);
	}

	/**
	 *  Returns the name of the databases from the server.
	 *  
	 *  Example:
	 *  | Get Databases |
	 */	
	public List<String> getDatabases() {
		return mongoClient.getDatabaseNames();
	}

	
}
