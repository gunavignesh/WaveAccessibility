package runner;

import org.testng.Assert;

import org.testng.annotations.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class MongoDBTest {
	@Test
	public void mongoTest() {
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("test");
			DBCollection dbCollection = db.getCollection("cpo");
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("num", "INV661970");
			DBCursor cursor = dbCollection.find(searchQuery);
			String response="";
			try {
				while (cursor.hasNext()) {
					response = response.concat(cursor.next().toString());
				}
			} finally {
				cursor.close();
			}
			Assert.assertTrue(response.contains("QZpWEQvKjafjBggK6"));
		} catch (Exception e) {
			//System.out.println(e.getMessage());
		}
	}
}
