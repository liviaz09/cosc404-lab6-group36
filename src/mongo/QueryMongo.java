package mongo;

import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.include;

import java.nio.file.DirectoryStream.Filter;

import static com.mongodb.client.model.Projections.fields;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.platform.console.shadow.picocli.CommandLine.Help.Ansi.Text;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

/**
 * Program to create a collection, insert JSON objects, and perform simple queries on MongoDB.
 */
public class QueryMongo
{	
	/**
	 * MongoDB database name
	 */
	public static final String DATABASE_NAME = "lab6";
	
	/**
	 * MongoDB collection name
	 */
	public static final String COLLECTION_NAME = "data";
	
	/**
	 * MongoDB Server URL
	 */
	private static final String SERVER = "localhost";
	
	/**
	 * Mongo client connection to server
	 */
	private MongoClient mongoClient;
	
	/**
	 * Mongo database
	 */
	private MongoDatabase db;
	
	
	/**
	 * Main method
	 * 
	 * @param args
	 * 			no arguments required
	 */	
    public static void main(String [] args)
	{
    	QueryMongo qmongo = new QueryMongo();
    	qmongo.connect();
    	qmongo.load();
    	System.out.println(QueryMongo.toString(qmongo.query()));
    	qmongo.update(3);
    	System.out.println(QueryMongo.toString(qmongo.query()));
    	System.out.println(QueryMongo.toString(qmongo.query1()));
    	System.out.println(QueryMongo.toString(qmongo.query2()));
	}
        
    /**
     * Connects to Mongo database and returns database object to manipulate for connection.
     *     
     * @return
     * 		Mongo database
     */
    public MongoDatabase connect()
    {
    	try
		{
		    // Provide connection information to MongoDB server 
		    mongoClient = MongoClients.create("mongodb://lab6:404mgbpw@"+SERVER);
		}
	    catch (Exception ex)
		{	System.out.println("Exception: " + ex);
			ex.printStackTrace();
		}	
		
        // Provide database information to connect to		 
	    // Note: If the database does not already exist, it will be created automatically.
    	db = mongoClient.getDatabase(DATABASE_NAME);
		return db;
    }
    
    /**
     * Loads some sample data into MongoDB.
     */
    public void load()
    {					
		MongoCollection<Document> col;
		// Drop an existing collection (done to make sure you create an empty, new collection each time)
		col = db.getCollection(COLLECTION_NAME);
		if (col != null)
			col.drop();
		
		// TODO: Create a collection called "data". Note only need to get the collection as will be created if does not exist.
		// See: https://docs.mongodb.com/manual/reference/method/db.createCollection/
		// See: https://docs.mongodb.com/drivers/java/sync/current/fundamentals/databases-collections/	
		db.createCollection("data");
		
		// TODO: Add 5 objects to collection of the form: key, name, num, values
		// 		- where key is an increasing integer starting at 1 (i.e. 1, 2, 3, ...)
		//		- name is "text"+key (e.g. "text1")
		//		- num is key  (e.g. 1)
		//		- values is an array of 3 objects of the form: {"val":1, "text":"text1"}, {"val":2, "text":"text2"}, {"val":3, "text":"text3"}
		//			- The example is above for key = 1, for key = 2 the values should be 2,3,4, etc.
		// See: https://docs.mongodb.com/drivers/java/sync/current/quick-start/
		// See: https://docs.mongodb.com/manual/reference/method/db.collection.insert/
		// See: https://mongodb.github.io/mongo-java-driver/4.4/apidocs/mongodb-driver-core/com/mongodb/BasicDBList.html
    
    MongoCollection<Document> data = db.getCollection("data");
		for(int i =1;i<=5;i++){
			int key =i;
			String name = "text"+key;
			int num = key;
			BasicDBList values= new BasicDBList();
        values.add(new BasicDBObject("val", key).append("text", "text"+(key)));
        values.add(new BasicDBObject("val", key+1).append("text", "text"+(key+1)));
        values.add(new BasicDBObject("val", key+2).append("text", "text"+(key+2)));
        data.insertOne(new Document("key", key).append("name", name).append("num", num).append("values", values));
		}
		//([{key: key, name: name, num: key, values: {val: key, text:name}, {val: key++, text}}]);


					   
	}
    
    /**
     * Updates a MongoDB record with given key so that the key is 10 times bigger.  The name field should also be updated with the new key value (e.g. text10).
     */
	public void update(int key) 
	{
		// TODO: Update document in collection with given key. Modify key to be 10 times bigger and update the name field to have the new key value.
		// See: https://docs.mongodb.com/drivers/java/sync/current/usage-examples/updateOne/		

		MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);
		//get the record and save it as a document
		BasicDBObject fil = new BasicDBObject("key",key);
		BasicDBObject up = new BasicDBObject("$mul", new BasicDBObject("key", 10))
		.append("$set", new BasicDBObject("name", "text" + (key * 10)));
		col.updateMany(fil, up);
	}
    
    /**
     * Performs a MongoDB query that prints out all data (except for the _id).
     */
	public MongoCursor<Document> query() 
	{		
		MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);		
		
		// See: https://docs.mongodb.com/drivers/java/sync/current/usage-examples/find/
		
		MongoCursor<Document> cursor = col.find().projection(fields(include("key", "name", "num", "values"), excludeId())).iterator();
		// OR:
		// MongoCursor<Document> cursor = col.find().projection(excludeId()).iterator();				
		return cursor;				
	}
    
    /**
     * Performs a MongoDB query that returns all documents with key < 4.  Only show the key, name, and num fields.
     */
    public MongoCursor<Document> query1()
    {
    	// TODO: Write a MongoDB query that returns all documents with key < 4.  Only show the key, name, and num fields.
    	// Use the code in query() method as a starter.  You must return a cursor to the results.
    	// See: https://docs.mongodb.com/drivers/java/sync/current/usage-examples/find/
    	
		MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);	
		// get a filter for key <4
		MongoCursor<Document> cur = col.find(Filters.lt("key",4)).projection(Projections.fields(Projections.include("key","name","num"),Projections.excludeId())).iterator();
		return cur; 		// Note: Need to modify query as this is currently returning all documents		
    }
    
    /**
     * Performs a MongoDB query that returns all documents with key > 2 OR contains an element in the array with val = 4. 
     */
    public MongoCursor<Document> query2()
    {    	
    	// TODO: Write a MongoDB query that returns all documents with key > 2 OR contains an element in the array with val = 4. 
    	// Use the code in query() method as a starter.  You must return a cursor to the results.
    	// See: https://docs.mongodb.com/manual/reference/operator/query/or/    	
    	// See: https://docs.mongodb.com/manual/reference/operator/query/elemMatch/

    	MongoCollection<Document> col = db.getCollection(COLLECTION_NAME);	
		MongoCursor<Document> cur = col.find(Filters.or(Filters.gt("key",2),Filters.elemMatch("values", Filters.eq("val", 4)))).iterator();
		return cur; 	
		// Note: Need to modify query as this is currently returning all documents				
    }          
    
    /**
     * Returns the Mongo database being used.
     * 
     * @return
     * 		Mongo database
     */
    public MongoDatabase getDb()
    {
    	return db;    
    }
    
    /**
     * Outputs a cursor of MongoDB results in string form.
     * 
     * @param cursor
     * 		Mongo cursor
     * @return
     * 		results as a string
     */
    public static String toString(MongoCursor<Document> cursor)
    {
    	StringBuilder buf = new StringBuilder();
    	int count = 0;
    	buf.append("Rows:\n");
    	if (cursor != null)
    	{	    	
			while (cursor.hasNext()) {
				Document obj = cursor.next();
				buf.append(obj.toJson());
				buf.append("\n");
				count++;
			}
    	}
		buf.append("Number of rows: "+count);
		cursor.close();
		return buf.toString();
    }
} 
