package de.codecentric.robot.mongodblibrary.keywords;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class MongodbLibraryTest {

	private MongodbLibrary library;
	private MongoClient mongoClient;
	private DB db;
	
	@Before
	public void setUp() throws UnknownHostException {
		library = new MongodbLibrary();
		mongoClient = new MongoClient("localhost" , 27017 );
		db = mongoClient.getDB("robotdb");
	}
	
	@Test
	public void shouldInsertJsonIntoCollection() {
		//given
		DBCollection collection = db.getCollection("testCol");
		String json = "{say : 'Hello MongoDb!'}";
		//when
		library.insertJsonDocumentIntoCollection("testCol", json);
		//then
		DBObject object = collection.find().next();
		assertThat(object, is(notNullValue()));
		assertThat(object.containsField("say"), is(Boolean.TRUE));
		assertThat((String)object.get("say"), is("Hello MongoDb!"));
	}
	
	@After
	public void tearDown() {
		db.getCollection("testCol").drop();
	}
}
