package task2;

import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream.Filter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;

public class MongoHandler {
	
	private static MongoClient mongoClient;
	private static MongoDatabase db;
	private static MongoCollection<Document> collection;
	private static MongoCollection<Document> ie_collection;
	
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
		Document document = collection.find(Filters.and(Filters.eq("username", username), Filters.eq("password", pwd))).first();
		if (document == null) {
			return null;
		} else {
			user = new JSONObject(document.toJson());
			User u = new User(user.get("username").toString(), user.get("password").toString(), 
					user.get("company_name").toString(), user.get("address").toString(), 
					user.get("country").toString(), user.get("email").toString(), 
					user.get("number").toString(), user.get("core_business").toString());
			return u;
		}
	}
	
	public static User getUserByUsername(String username) {
		collection = db.getCollection("users");
		MongoCursor<Document> cursor = collection.find().iterator();

		JSONObject user;
		Document document = collection.find(Filters.eq("username", username)).first();
		if (document == null) {
			return null;
		} else {
			user = new JSONObject(document.toJson());
			User u = new User(user.get("username").toString(), user.get("password").toString(), 
					user.get("company_name").toString(), user.get("address").toString(), 
					user.get("country").toString(), user.get("email").toString(), 
					user.get("number").toString(), user.get("core_business").toString());
			return u;
		}
	}
	
	public static void changeInformation(User new_user, String old_username) {
		
		collection = db.getCollection("users");
		Document query = new Document("username", new_user.getUsername())
					.append("password", new_user.getPassword())
					.append("company_name", new_user.getCompanyName())
					.append("address", new_user.getAddress())
					.append("email", new_user.getEmail())
					.append("country", new_user.getCountry())
					.append("number", new_user.getNumber())
					.append("core_business", new_user.getCoreBusiness());
		
		Document updateQuery = new Document("$set", query);
													
		UpdateResult result = collection.updateOne(Filters.eq("username", old_username), updateQuery);
		System.out.println(result.getModifiedCount());
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
	
	public static void deleteAccountByUsername(String username) {
		collection = db.getCollection("users");
		
		DeleteResult result = collection.deleteOne(Filters.eq("username", username));
		System.out.println(result);
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
	
	
	/*
	 * QUERY
	 */
	public static int getTotalProduction(String food, String region, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		AggregateIterable<Document> documents;
		Document groupFields;
				
		if(country == null && region != null) {
			multiIdMap.put("Region", "$countries.region");
			multiIdMap.put("Food", "$name");

			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_region",region), 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.sum("TotalProduction", "$countries.years.production")
		                      )
				      )
			);
		}
		else {
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");

			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_name", country),
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.sum("TotalProduction", "$countries.years.production")
		                      )
				      )
			);
		}
			
		List<Document> docs = intoList(documents);
		return Integer.parseInt(docs.get(0).get("TotalProduction").toString());
	}
	
	public static Double getAverageProduction(String food, String region, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		AggregateIterable<Document> documents;
		Document groupFields;
				
		if(country == null && region != null) {
			multiIdMap.put("Region", "$countries.region");
			multiIdMap.put("Food", "$name");

			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_region",region), 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.avg("AvgProduction", "$countries.years.production")
		                      )
				      )
			);
		}
		else {
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");

			groupFields = new Document(multiIdMap);

			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_name", country),
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.avg("AvgProduction", "$countries.years.production")
		                      )
				      )
			);
		}
			
		List<Document> docs = intoList(documents);
		return Double.parseDouble(docs.get(0).get("AvgProduction").toString());
	}
	
	public static int getTotalImport(String food, String region, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		JSONObject obj ;
		int TotalImport = 0;
		
		MongoCursor<Document> cursor;
						
		if(country == null && region != null) {			
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_region",region), 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))
				      )
			).iterator();
		}
		else {
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_name", country),
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))				             
				      )
			).iterator();
		}
			
		try {
			while (cursor.hasNext()) {
				obj = new JSONObject(cursor.next().toJson());
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = y.getJSONObject("id_ie");
				
				Document document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
				if (document == null) {
				    //Document does not exist
				} else {
					JSONObject d = new JSONObject(document.toJson());
					if(d.has("import_qty")) {
						TotalImport += (int) d.get("import_qty");
					}
				}
			}
		} finally {
			cursor.close();
		}
		return TotalImport;
	}
	
	public static Double getAverageImport(String food, String region, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		JSONObject obj ;
		Double year_selected = Double.parseDouble(end) - Double.parseDouble(start);
		Double AvgImport = 0.0;
		
		MongoCursor<Document> cursor;
						
		if(country == null && region != null) {			
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_region",region), 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))
				      )
			).iterator();
		}
		else {
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_name", country),
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))				             
				      )
			).iterator();
		}
			
		try {
			while (cursor.hasNext()) {
				obj = new JSONObject(cursor.next().toJson());
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = y.getJSONObject("id_ie");
				
				Document document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
				if (document == null) {
				    //Document does not exist
				} else {
					JSONObject d = new JSONObject(document.toJson());
					if(d.has("import_qty")) {
						AvgImport += (int) d.get("import_qty");
					}
				}
			}
		} finally {
			cursor.close();
		}
		
		return AvgImport/year_selected;
	}
	
	public static int getTotalExport(String food, String region, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		JSONObject obj ;
		int TotalExport = 0;
		
		MongoCursor<Document> cursor;
						
		if(country == null && region != null) {			
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_region",region), 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))
				      )
			).iterator();
		}
		else {
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_name", country),
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))				             
				      )
			).iterator();
		}
			
		try {
			while (cursor.hasNext()) {
				obj = new JSONObject(cursor.next().toJson());
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = y.getJSONObject("id_ie");
				
				Document document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
				if (document == null) {
				    //Document does not exist
				} else {
					JSONObject d = new JSONObject(document.toJson());
					if(d.has("export_qty")) {
						TotalExport += (int) d.get("export_qty");
					}
				}
			}
		} finally {
			cursor.close();
		}
		return TotalExport;
	}
	
	public static Double getAverageExport(String food, String region, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		JSONObject obj ;
		Double year_selected = Double.parseDouble(end) - Double.parseDouble(start);
		Double AvgExport = 0.0;
		
		MongoCursor<Document> cursor;
						
		if(country == null && region != null) {			
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_region",region), 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))
				      )
			).iterator();
		}
		else {
			cursor = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(Filters.eq("name", food), 
				            		  Filters.eq("countries.country_name", country),
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  ))				             
				      )
			).iterator();
		}
			
		try {
			while (cursor.hasNext()) {
				obj = new JSONObject(cursor.next().toJson());
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = y.getJSONObject("id_ie");
				
				Document document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
				if (document == null) {
				    //Document does not exist
				} else {
					JSONObject d = new JSONObject(document.toJson());
					if(d.has("import_qty")) {
						AvgExport += (int) d.get("import_qty");
					}
				}
			}
		} finally {
			cursor.close();
		}
		
		return AvgExport/year_selected;
	}
	
	/*
	 * UTILITY FUNCTIONS
	 */
	private static List<Document> intoList(MongoIterable<Document> documents) {
        List<Document> users = new ArrayList<>();
        documents.into(users);
        return users;
    }
}
