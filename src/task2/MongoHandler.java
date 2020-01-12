package task2;

import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;

public class MongoHandler {
	
	private static MongoClient mongoClient;
	private static MongoDatabase db;
	private static MongoCollection<Document> collection;
	
	public static void startMongo() {
		System.out.println("start MongoDB");
		mongoClient=MongoClients.create("mongodb://localhost:27017");
		db = mongoClient.getDatabase("test");
		
	}
	
	public static void closeMongo() {
		System.out.println("close MongoDB");
		mongoClient.close();
	}

	
	/*
	 * USER FUNCTIONS
	 */
	
	public static User checkUserCredential(String username, String pwd){
		collection = db.getCollection("users");
		MongoCursor<Document> cursor = collection.find().iterator();

		JSONObject user;
		try {
			while (cursor.hasNext()) {
				user = new JSONObject(cursor.next().toJson());
				//System.out.println(user);
				if(user.get("username").equals(username) && user.get("password").equals(pwd)) {
					User u=new User(user.get("username").toString(), user.get("password").toString(), 
							user.get("company_name").toString(), user.get("address").toString(), 
							user.get("country").toString(), user.get("email").toString(), 
							user.get("number").toString(), user.get("core_business").toString());
					return u;
				}
			}
		} finally {
			cursor.close();
		}
		return null;
	}
	
	public static void insertUser(User u){
		collection = db.getCollection("users");
		
		Document doc = new Document();
		
		doc.append("username", u.getUsername());
		doc.append("password", u.getPassword());
		doc.append("company_name", u.getCompanyName());
		doc.append("address", u.getAddress());
		doc.append("country", u.getCountry());
		doc.append("email", u.getEmail());
		doc.append("number", u.getNumber());
		doc.append("core_business", u.getCoreBusiness());
		
		collection.insertOne(doc);
	}
	
	/*
	 * FOOD FUNCTIONS
	 */
	
	public static List<String> getFood(){
		collection = db.getCollection("dataModelArrAvg");
		
		MongoCursor<Document> cursor = collection.find().iterator();

		JSONObject food;		
		List<String> food_list = new ArrayList<String>();
		try {
			while (cursor.hasNext()) {
				food = new JSONObject(cursor.next().toJson());
				//System.out.println(food.get("name"));
				food_list.add((String) food.get("name"));
			}
		} finally {
			cursor.close();
		}
		return food_list;
	}
	
	
}
