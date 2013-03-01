package de.codecentric.robot.mongodblibrary.keywords;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
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
	 * Connecting to a specific mongodb server with given database.
	 */
	public MongodbLibrary(String server, String database) {
		this(server, 27017, database);
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
			mongoClient = new MongoClient(server, port);
			db = mongoClient.getDB(database);
		} catch (UnknownHostException e) {
			throw new MongodbLibraryException("error connecting mongodb", e);
		}
	}

	/**
	 * Inserts the given Json-Document into the given collection.
	 * 
	 * Arguments: 
	 * - _collectionName_: the name of the target collection 
	 * - _jsonString_: the json document to persist
	 * 
	 * Example: 
	 * | Insert Document | myCollection | {say : 'Hello MongoDB!'} |
	 */
	public void insertDocument(String collectionName,
			String jsonString) {
		db.getCollection(collectionName).insert(
				(DBObject) JSON.parse(jsonString));
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
			String documents = readFileAsString(file);
			DBObject dbObject = (DBObject) JSON.parse(documents);
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
			String documents = readFileAsString(file);
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
	public void createCollection(String collectionName, String options) {
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
	public void ensureIndex(String indexName, String collectionName, String keys
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
	
	DB getDb() {
		return db;
	}
	
    private static String readFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }



}
