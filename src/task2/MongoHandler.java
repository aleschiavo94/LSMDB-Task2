package task2;

import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream.Filter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UnwindOptions;
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
					User u = new User(user.get("username").toString(), user.get("password").toString(), 
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
	
	public static User getUserByUsername(String username) {
		collection = db.getCollection("users");
		MongoCursor<Document> cursor = collection.find().iterator();

		JSONObject user;
		try {
			while (cursor.hasNext()) {
				user = new JSONObject(cursor.next().toJson());
				//System.out.println(user);
				if(user.get("username").equals(username)) {
					User u = new User(user.get("username").toString(), user.get("password").toString(), 
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
	
	public static boolean changeInformation(User new_user) {
		collection = db.getCollection("users");
		MongoCursor<Document> cursor = collection.find().iterator();
		
		JSONObject user;
		try {
			while (cursor.hasNext()) {
				user = new JSONObject(cursor.next().toJson());
				//System.out.println(user);
				if(user.get("username").equals(new_user.getUsername())) {
					user.put("username", new_user.getUsername());
					user.put("password", new_user.getPassword());
					user.put("company_name", new_user.getCompanyName());
					user.put("address", new_user.getAddress());
					user.put("email", new_user.getEmail());
					user.put("country", new_user.getCountry());
					user.put("number", new_user.getNumber());
					user.put("core_business", new_user.getCoreBusiness());
					return true;
				}
			}
		} finally {
			cursor.close();
		}
		return false;
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
//				System.out.println(food.get("countries.country_name"));
				food_list.add((String) food.get("name"));
			}
		} finally {
			cursor.close();
		}
		return food_list;
	}
	
	public static void getQueryResult(String food, String region, String country, String aim, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		
		if(country == null && region != null) {
			MongoCursor<Document> cursor = collection.aggregate(
				      Arrays.asList(
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_region",region) 
//				            		  Filters.gte("countries.years.year", start) 
//				            		  Filters.lte("countries.years.year", end)
				            		  ))
				      )
			).iterator();
			try {
				while (cursor.hasNext()) {
					System.out.println(cursor.next().toJson());
				}
			} finally {
				cursor.close();
			}
		}
		else {
			MongoCursor<Document> cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_name",country), 
				            		  Filters.gte("countries.years.year", start),
				            		  Filters.lte("countries.years.year", end)
				            		  ))
				      )
			).iterator();
			try {
				while (cursor.hasNext()) {
					System.out.println(cursor.next().toJson());
				}
			} finally {
				cursor.close();
			}
		}
	}
	
}
