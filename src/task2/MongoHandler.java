package task2;

import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream.Filter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
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
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileWriter;

public class MongoHandler {
	
	private static MongoClient mongoClient;
	private static MongoDatabase db;
	private static MongoCollection<Document> collection;
	private static MongoCollection<Document> ie_collection;
	
	public static void startMongo() {
		System.out.println("start MongoDB");
		mongoClient=MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=Replicas");
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
			User u; 
			if(user.length()==3) {
				u= new User(user.getString("username").toString(),
						user.getString("password").toString());
			}else {
				u = new User(user.get("username").toString(), user.get("password").toString(), 
						user.get("company_name").toString(), user.get("address").toString(), 
						user.get("country").toString(), user.get("email").toString(), 
						user.get("number").toString(), user.get("core_business").toString());
						
			}
			
			return u;
		}
	}
	
	public static User getUserByUsername(String username) {
		collection = db.getCollection("users");

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
	
	public static List<String> getAllUsers(){
		collection = db.getCollection("users");
		
		MongoCursor<Document> cursor = collection.find().iterator();

		JSONObject user;		
		List<String> user_list = new ArrayList<String>();
		try {
			while (cursor.hasNext()) {
				user = new JSONObject(cursor.next().toJson());
				if(!user.get("username").toString().equals("admin")) {
					user_list.add(user.get("username").toString());
				}
			}
		} finally {
			cursor.close();
		}
		return user_list;
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
		collection = db.getCollection("users").withWriteConcern(WriteConcern.MAJORITY);
		Document concern = new Document();
		concern.append("w", "majority");
		concern.append("wtimeout", 5000);
		
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
		collection = db.getCollection("users").withWriteConcern(WriteConcern.MAJORITY);
		
		DeleteResult result = collection.deleteOne(Filters.eq("username", username));
		System.out.println(result);
	}
	
	/*
	 * FOOD FUNCTIONS
	 */	
	public static List<String> getFood(){
		collection = db.getCollection("data");
		
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
	
	public static int insertDocument(String str) {
		JSONObject json = new JSONObject(str);
		Document query = null;
		Document push_element = null;
		
		ie_collection = db.getCollection("impExpInfo").withWriteConcern(WriteConcern.MAJORITY);
		
		//inserisco l'import e l'export
		Document imp_exp = new Document();
		if(!json.getString("import_qty").equals("0.0"))
			imp_exp.append("import_qty", Double.parseDouble(json.getString("import_qty")));
		if(!json.getString("import_value").equals("0.0"))
			imp_exp.append("import_value", Double.parseDouble(json.getString("import_value")));
		if(!json.getString("export_qty").equals("0.0"))
			imp_exp.append("export_qty", Double.parseDouble(json.getString("export_qty")));
		if(!json.getString("export_value").equals("0.0"))
			imp_exp.append("export_value", Double.parseDouble(json.getString("export_value")));
		
		System.out.println(imp_exp);

		ie_collection.insertOne(imp_exp);
		ObjectId id_ie = imp_exp.getObjectId("_id");
		
		collection = db.getCollection("data").withWriteConcern(WriteConcern.MAJORITY);
		//controllo se esiste lo stato tra gli stati di quel food		
		Bson filters = Filters.and(Filters.eq("name", json.getString("name")), Filters.eq("countries.country_name", json.getString("country_name")));
		Document document = collection.find(filters).first();
				
		if (document == null) {
			Document doc2 = new Document("year", Integer.parseInt(json.getString("year")))
					.append("rain", json.getString("rain"))
					.append("production", Integer.parseInt(json.getString("production")))
					.append("temperature_avg", Double.parseDouble(json.getString("temperature_avg")))
					.append("temperature", json.getString("temperature"))
					.append("rainfall_avg", Double.parseDouble(json.getString("rainfall_avg")))
					.append("id_ie", id_ie);
			Document doc1 = new Document("country_name", json.getString("country_name"))
					.append("years", doc2);
			
			query = new Document("countries", doc1);
			filters = Filters.eq("name", json.getString("name"));
			
		} else {
			Document doc = new Document("year", Integer.parseInt(json.getString("year")))
					.append("rain", json.getString("rain"))
					.append("production", Integer.parseInt(json.getString("production")))
					.append("temperature_avg", Double.parseDouble(json.getString("temperature_avg")))
					.append("temperature", json.getString("temperature"))
					.append("rainfall_avg", Double.parseDouble(json.getString("rainfall_avg")));
		
			query = new Document("countries.$.years", doc);
		}
		
		push_element = new Document("$push", query);
		UpdateResult result = collection.updateOne(filters, push_element); //Updates.addToSet("countries.years", updateQuery));
		System.out.println(result.getModifiedCount());
		return (int) result.getModifiedCount();
	}
	
	
	/*
	 * QUERY PRODUCTION
	 */
	public static JSONArray getTotalProduction(String food, String region, String country, String start, String end) {
		collection = db.getCollection("data");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		MongoCursor<Document> documents;
		Document groupFields;
		Bson matchFields;
				
		if(country == null && region != null) {
			
			if(!region.equals("World")) {
				matchFields = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
			}
			else 
				matchFields = Filters.and(Filters.eq("name", food));
			
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");
			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(matchFields, 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.sum("TotalProduction", "$countries.years.production"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		else {
			multiIdMap.put("Year", "$countries.years.year");
			multiIdMap.put("Country", "$countries.country_name");

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
				                      Accumulators.sum("TotalProduction", "$countries.years.production"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
		
		try {
			while (documents.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(documents.next().toJson());
				
				JSONObject id = obj.getJSONObject("_id");
				if(obj.getInt("TotalProduction") != 0) {
					country_result.put("TotalProduction", obj.getInt("TotalProduction"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country_result.put("AvgTemperature", 0.0);
					else
						country_result.put("AvgTemperature", obj.get("AvgTemperature"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country_result.put("AvgPrecipitation", 0.0);
					else
						country_result.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					
					if(id.has("Year"))
						country_result.put("Year", id.getInt("Year"));
					country_result.put("Country", id.getString("Country"));
					
					result.put(i, country_result);
					i++;
				}
			}
		} finally {
			documents.close();
		}	
		return result;
	}
	
	public static JSONArray getAverageProduction(String food, String region, String country, String start, String end) {
		collection = db.getCollection("data");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		MongoCursor<Document> documents;
		Document groupFields;
		Bson groupFilters;
				
		if(country == null && region != null) {
			if(!region.equals("World")) {
				groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
			}
			else 
				groupFilters = Filters.and(Filters.eq("name", food));
			
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");
			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(groupFilters, 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.avg("AvgProduction", "$countries.years.production"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		else {
			multiIdMap.put("Year", "$countries.years.year");
			multiIdMap.put("Country", "$countries.country_name");

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
				                      Accumulators.avg("AvgProduction", "$countries.years.production"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
			
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
		
		try {
			while (documents.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(documents.next().toJson());
				JSONObject id = obj.getJSONObject("_id");
				
				if(obj.get("AvgProduction") != null || obj.getDouble("AvgProduction") != 0.0) {
					country_result.put("AvgProduction", obj.get("AvgProduction"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country_result.put("AvgTemperature", 0.0);
					else
						country_result.put("AvgTemperature", obj.get("AvgTemperature"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country_result.put("AvgPrecipitation", 0.0);
					else
						country_result.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					if(id.has("Year"))
						country_result.put("Year", id.getInt("Year"));
					country_result.put("Country", id.getString("Country"));
					
					result.put(i, country_result);
					i++;
				}
			}
		} finally {
			documents.close();
		}	
		return result;
	}
	
	public static JSONArray getTop5Production(String food, String region, String start, String end) {
		collection = db.getCollection("data");
		
		JSONObject obj = new JSONObject();
		JSONArray totalCountry = new JSONArray();
		int TotalProduction = 0;
		Double AvgPrecipitation = 0.0;
		Double AvgTemperature = 0.0;
		Double year_selected = (Double.parseDouble(end) - Double.parseDouble(start)) + 1.0;
		
		Bson groupFilters;
		if(!region.equals("World")) 
			groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
		else
			groupFilters = Filters.and(Filters.eq("name", food));
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		multiIdMap.put("Country", "$countries.country_name");
		multiIdMap.put("Food", "$name");
		Document groupFields = new Document(multiIdMap);
		
		MongoCursor<Document> cursor = collection.aggregate(
			      Arrays.asList(
			    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			              Aggregates.match(Filters.and(groupFilters, 
			            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
			            		  Filters.lte("countries.years.year", Integer.parseInt(end))
			            		  )),
			              Aggregates.group(groupFields,
			            		  Accumulators.sum("TotalProduction", "$countries.years.production"),
			                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
			                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
	                      ),
			              Aggregates.sort(Sorts.descending("TotalProduction"))
			      )
		).iterator();
		
		int i = 0;
		try {
			while (cursor.hasNext()) {
				JSONObject country = new JSONObject();
				obj = new JSONObject(cursor.next().toJson());
				
				JSONObject id = obj.getJSONObject("_id");
				
				if(obj.getInt("TotalProduction") != 0) {
					country.put("Country", id.get("Country"));
					country.put("TotalProduction", obj.get("TotalProduction"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country.put("AvgTemperature", 0.0);
					else
						country.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country.put("AvgPrecipitation", 0.0);
					else
						country.put("AvgTemperature", obj.get("AvgTemperature"));
	
					totalCountry.put(i, country);
					if(i == 4) 
						break;
					else
						i++;
				}
			}
		} finally {
			cursor.close();
		}
		return totalCountry;
	}
	
	
	/*
	 * QUERY IMPORT
	 */
	public static JSONArray getTotalImport(String food, String country, String region, String start, String end) {
		collection = db.getCollection("data");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		MongoCursor<Document> documents;
		Document groupFields;
		Bson matchFields;
				
		if(country == null && region != null) {
			
			if(!region.equals("World")) {
				matchFields = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
			}
			else 
				matchFields = Filters.and(Filters.eq("name", food));
			
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");
			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(matchFields, 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.sum("Import", "$countries.years.import_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		else {
			multiIdMap.put("Year", "$countries.years.year");
			multiIdMap.put("Country", "$countries.country_name");

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
				                      Accumulators.sum("Import", "$countries.years.import_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
		
		try {
			while (documents.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(documents.next().toJson());
				System.out.println(obj);
				JSONObject id = obj.getJSONObject("_id");
				if(obj.getInt("Import") != 0) {
					country_result.put("Import", obj.getInt("Import"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country_result.put("AvgTemperature", 0.0);
					else
						country_result.put("AvgTemperature", obj.get("AvgTemperature"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country_result.put("AvgPrecipitation", 0.0);
					else
						country_result.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					
					if(id.has("Year"))
						country_result.put("Year", id.getInt("Year"));
					country_result.put("Country", id.getString("Country"));
					
					result.put(i, country_result);
					i++;
				}
			}
		} finally {
			documents.close();
		}	
		return result;
	}
	
	public static JSONArray getAverageImport(String food, String country, String region, String start, String end) {
		collection = db.getCollection("data");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		MongoCursor<Document> documents;
		Document groupFields;
		Bson groupFilters;
				
		if(country == null && region != null) {
			if(!region.equals("World")) {
				groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
			}
			else 
				groupFilters = Filters.and(Filters.eq("name", food));
			
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");
			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(groupFilters, 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.avg("Import", "$countries.years.import_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		else {
			multiIdMap.put("Year", "$countries.years.year");
			multiIdMap.put("Country", "$countries.country_name");

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
				                      Accumulators.avg("Import", "$countries.years.import_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
			
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
		
		try {
			while (documents.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(documents.next().toJson());
				JSONObject id = obj.getJSONObject("_id");
				
				if(obj.get("Import") != null || obj.getDouble("Import") != 0.0) {
					country_result.put("Import", obj.get("Import"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country_result.put("AvgTemperature", 0.0);
					else
						country_result.put("AvgTemperature", obj.get("AvgTemperature"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country_result.put("AvgPrecipitation", 0.0);
					else
						country_result.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					if(id.has("Year"))
						country_result.put("Year", id.getInt("Year"));
					country_result.put("Country", id.getString("Country"));
					
					result.put(i, country_result);
					i++;
				}
			}
		} finally {
			documents.close();
		}	
		return result;
	}
	
	public static JSONArray getTop5Import(String food, String region, String start, String end) {
		collection = db.getCollection("data");
		
		JSONObject obj = new JSONObject();
		JSONArray totalCountry = new JSONArray();
		int Import = 0;
		Double AvgPrecipitation = 0.0;
		Double AvgTemperature = 0.0;
		Double year_selected = (Double.parseDouble(end) - Double.parseDouble(start)) + 1.0;
		
		Bson groupFilters;
		if(!region.equals("World")) 
			groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
		else
			groupFilters = Filters.and(Filters.eq("name", food));
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		multiIdMap.put("Country", "$countries.country_name");
		multiIdMap.put("Food", "$name");
		Document groupFields = new Document(multiIdMap);
		
		MongoCursor<Document> cursor = collection.aggregate(
			      Arrays.asList(
			    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			              Aggregates.match(Filters.and(groupFilters, 
			            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
			            		  Filters.lte("countries.years.year", Integer.parseInt(end))
			            		  )),
			              Aggregates.group(groupFields,
			            		  Accumulators.sum("Import", "$countries.years.import_qty"),
			                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
			                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
	                      ),
			              Aggregates.sort(Sorts.descending("Import"))
			      )
		).iterator();
		
		int i = 0;
		try {
			while (cursor.hasNext()) {
				JSONObject country = new JSONObject();
				obj = new JSONObject(cursor.next().toJson());
				
				JSONObject id = obj.getJSONObject("_id");
				
				if(obj.getInt("Import") != 0) {
					country.put("Country", id.get("Country"));
					country.put("Import", obj.get("Import"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country.put("AvgTemperature", 0.0);
					else
						country.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country.put("AvgPrecipitation", 0.0);
					else
						country.put("AvgTemperature", obj.get("AvgTemperature"));
	
					totalCountry.put(i, country);
					if(i == 4) 
						break;
					else
						i++;
				}
			}
		} finally {
			cursor.close();
		}
		return totalCountry;
	}
	
	
	
	/*
	 * QUERY EXPORT
	 */
	public static JSONArray getTotalExport(String food, String country, String region, String start, String end) {
		collection = db.getCollection("data");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		MongoCursor<Document> documents;
		Document groupFields;
		Bson matchFields;
				
		if(country == null && region != null) {
			
			if(!region.equals("World")) {
				matchFields = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
			}
			else 
				matchFields = Filters.and(Filters.eq("name", food));
			
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");
			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(matchFields, 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.sum("Export", "$countries.years.export_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		else {
			multiIdMap.put("Year", "$countries.years.year");
			multiIdMap.put("Country", "$countries.country_name");

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
				                      Accumulators.sum("Export", "$countries.years.export_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
		
		try {
			while (documents.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(documents.next().toJson());
				System.out.println(obj);
				JSONObject id = obj.getJSONObject("_id");
				if(obj.getInt("Export") != 0) {
					country_result.put("Export", obj.getInt("Export"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country_result.put("AvgTemperature", 0.0);
					else
						country_result.put("AvgTemperature", obj.get("AvgTemperature"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country_result.put("AvgPrecipitation", 0.0);
					else
						country_result.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					
					if(id.has("Year"))
						country_result.put("Year", id.getInt("Year"));
					country_result.put("Country", id.getString("Country"));
					
					result.put(i, country_result);
					i++;
				}
			}
		} finally {
			documents.close();
		}	
		return result;
	}
	
	public static JSONArray getAverageExport(String food, String country, String region, String start, String end) {
		collection = db.getCollection("data");
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		MongoCursor<Document> documents;
		Document groupFields;
		Bson groupFilters;
				
		if(country == null && region != null) {
			if(!region.equals("World")) {
				groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
			}
			else 
				groupFilters = Filters.and(Filters.eq("name", food));
			
			multiIdMap.put("Country", "$countries.country_name");
			multiIdMap.put("Food", "$name");
			groupFields = new Document(multiIdMap);
			
			documents = collection.aggregate(
				      Arrays.asList(
				    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
				              Aggregates.match(Filters.and(groupFilters, 
				            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
				            		  Filters.lte("countries.years.year", Integer.parseInt(end))
				            		  )),
				              Aggregates.group(groupFields,
				                      Accumulators.avg("Export", "$countries.years.export_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
		else {
			multiIdMap.put("Year", "$countries.years.year");
			multiIdMap.put("Country", "$countries.country_name");

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
				                      Accumulators.avg("Export", "$countries.years.export_qty"),
				                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
				                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
		                      )
				      )
			).iterator();
		}
			
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
		
		try {
			while (documents.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(documents.next().toJson());
				JSONObject id = obj.getJSONObject("_id");
				
				if(obj.get("Export") != null || obj.getDouble("Export") != 0.0) {
					country_result.put("Export", obj.get("Export"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country_result.put("AvgTemperature", 0.0);
					else
						country_result.put("AvgTemperature", obj.get("AvgTemperature"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country_result.put("AvgPrecipitation", 0.0);
					else
						country_result.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					if(id.has("Year"))
						country_result.put("Year", id.getInt("Year"));
					country_result.put("Country", id.getString("Country"));
					
					result.put(i, country_result);
					i++;
				}
			}
		} finally {
			documents.close();
		}	
		return result;
	}
	
	public static JSONArray getTop5Export(String food, String region, String start, String end) {
		collection = db.getCollection("data");
		
		JSONObject obj = new JSONObject();
		JSONArray totalCountry = new JSONArray();
		int Export = 0;
		Double AvgPrecipitation = 0.0;
		Double AvgTemperature = 0.0;
		Double year_selected = (Double.parseDouble(end) - Double.parseDouble(start)) + 1.0;
		
		Bson groupFilters;
		if(!region.equals("World")) 
			groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
		else
			groupFilters = Filters.and(Filters.eq("name", food));
		
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		multiIdMap.put("Country", "$countries.country_name");
		multiIdMap.put("Food", "$name");
		Document groupFields = new Document(multiIdMap);
		
		MongoCursor<Document> cursor = collection.aggregate(
			      Arrays.asList(
			    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			              Aggregates.match(Filters.and(groupFilters, 
			            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
			            		  Filters.lte("countries.years.year", Integer.parseInt(end))
			            		  )),
			              Aggregates.group(groupFields,
			            		  Accumulators.sum("Export", "$countries.years.export_qty"),
			                      Accumulators.avg("AvgPrecipitation", "$countries.years.rainfall_avg"),
			                      Accumulators.avg("AvgTemperature", "$countries.years.temperature_avg")
	                      ),
			              Aggregates.sort(Sorts.descending("Export"))
			      )
		).iterator();
		
		int i = 0;
		try {
			while (cursor.hasNext()) {
				JSONObject country = new JSONObject();
				obj = new JSONObject(cursor.next().toJson());
				
				JSONObject id = obj.getJSONObject("_id");
				
				if(obj.getInt("Export") != 0) {
					country.put("Country", id.get("Country"));
					country.put("Export", obj.get("Export"));
					if(obj.get("AvgTemperature").toString().equals("null"))
						country.put("AvgTemperature", 0.0);
					else
						country.put("AvgPrecipitation", obj.get("AvgPrecipitation"));
					if(obj.get("AvgPrecipitation").toString().equals("null"))
						country.put("AvgPrecipitation", 0.0);
					else
						country.put("AvgTemperature", obj.get("AvgTemperature"));
	
					totalCountry.put(i, country);
					if(i == 4) 
						break;
					else
						i++;
				}
			}
		} finally {
			cursor.close();
		}
		return totalCountry;
	}
	
}
