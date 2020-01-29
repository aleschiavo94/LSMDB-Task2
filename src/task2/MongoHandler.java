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
	
	public static int insertDocument(String str) {
		JSONObject json = new JSONObject(str);
		Document query = null;
		Document push_element = null;
		
		ie_collection = db.getCollection("impExpInfo");
		
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
		
		collection = db.getCollection("dataModelArrAvg");
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
		collection = db.getCollection("dataModelArrAvg");
		
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
		collection = db.getCollection("dataModelArrAvg");
		
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
				
				if(!obj.get("AvgProduction").toString().equals("null") || obj.getDouble("AvgProduction") != 0.0) {
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
		collection = db.getCollection("dataModelArrAvg");
		
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
	public static JSONArray getTotalCountryImport(String food, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		
		MongoCursor<Document> cursor = collection.aggregate(
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
				
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
			
		try {
			while (cursor.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(cursor.next().toJson());
				
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = null;
				if(y.has("id_ie")) {
					ie = y.getJSONObject("id_ie");
					
					Document document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
					if (document != null) {
						JSONObject d = new JSONObject(document.toJson());
						if(d.has("import_qty") && d.getInt("import_qty") != 0) {
							country_result.put("Import", d.get("import_qty"));
							if(y.has("temperature_avg"))
								if(y.get("temperature_avg").toString().equals("null"))
									country_result.put("AvgTemperature", 0.0);
								else
									country_result.put("AvgTemperature", y.get("temperature_avg"));
							
							if(y.has("rainfall_avg"))
								if(y.get("rainfall_avg").toString().equals("null"))
									country_result.put("AvgPrecipitation", 0.0);
								else
									country_result.put("AvgPrecipitation", y.get("rainfall_avg"));
							country_result.put("Year", y.getInt("year"));
							country_result.put("Country", c.getString("country_name"));	
							
							result.put(i, country_result);
							i++;
						}
					}	
				}
			}
		} finally {
			cursor.close();
		}
		
		return result;
	}
	
	public static JSONArray getTotalRegionImport(String food, String region, String start, String end, boolean top5) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		Double TotalImport = 0.0;
		Double AvgPrecipitation = 0.0;
		Double AvgTemperature = 0.0;
		Double year_selected = (Double.parseDouble(end) - Double.parseDouble(start)) + 1.0;
		
		Bson groupFilters;
					
		if(!region.equals("World")) {
			groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
		}
		else 
			groupFilters = Filters.and(Filters.eq("name", food));
		
		MongoCursor<Document> cursor = collection.aggregate(
			      Arrays.asList(
			    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			              Aggregates.match(Filters.and(groupFilters, 
			            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
			            		  Filters.lte("countries.years.year", Integer.parseInt(end))
			            		  ))
			      )
		).iterator();
		
		JSONArray result = new JSONArray();
		JSONObject country_result = new JSONObject();
		String prev_country = null;
		JSONObject obj;
		int prev = 0;
		int i = 0;
			
		try {
			while (cursor.hasNext()) {
				country_result = new JSONObject();
				Document document = null;
				obj = new JSONObject(cursor.next().toJson());

				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = null;
				if(y.has("id_ie")) {
					ie = y.getJSONObject("id_ie");
					
					if(prev == 0) {
						prev_country = c.getString("country_name");
						prev = 1;
					}
					
					if(!prev_country.equals(c.getString("country_name"))) {
						if(TotalImport != 0.0) {
							country_result.put("AvgTemperature", AvgTemperature/year_selected);
							country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
							country_result.put("Import", TotalImport);
							country_result.put("Country", prev_country);
							
							result.put(i, country_result);
							i++;
						}
						AvgTemperature = 0.0;
						AvgPrecipitation = 0.0;
						TotalImport = 0.0;

						prev_country = c.getString("country_name");
					}
					
					if(y.has("rainfall_avg"))
						if(!y.get("rainfall_avg").toString().equals("null"))
							AvgPrecipitation += Double.parseDouble(y.get("rainfall_avg").toString());
					
					if(y.has("temperature_avg"))
						if(!y.get("temperature_avg").toString().equals("null"))
							AvgTemperature += Double.parseDouble(y.get("temperature_avg").toString());
					
					document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
					if (document != null) {
						JSONObject d = new JSONObject(document.toJson());
						
						if(d.has("import_qty") && d.getInt("import_qty") != 0)
							TotalImport += Double.parseDouble(d.get("import_qty").toString());
					}
				}
			}
			if(TotalImport != 0.0) {
				country_result.put("AvgTemperature", AvgTemperature/year_selected);
				country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
				country_result.put("Import", TotalImport);
				country_result.put("Country", prev_country);
				
				result.put(i, country_result);
			}
			
		} finally {
			cursor.close();
		}
		
		JSONArray sortedJsonArray = new JSONArray();
		if(top5) {    

		    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		    for (int j = 0; j < result.length(); j++) {
		        jsonValues.add(result.getJSONObject(j));
		    }
		    Collections.sort(jsonValues, new Comparator<JSONObject>() {
		        @Override
		        public int compare(JSONObject a, JSONObject b) {
		        	int compare = 0;
		            int valA = a.getInt("Import");
		            int valB = b.getInt("Import");
	                compare = Integer.compare(valB, valA);
		            return compare;
		        }
		    });


		    int length = 5;
		    if(result.length() < 5) {
		    	length = result.length();
		    }
		    
		    for (int j = 0; j < length; j++) {
		        sortedJsonArray.put(j, jsonValues.get(j));
		    }
		    
		    result = sortedJsonArray;
		}
		
		return result;
	}
	
	public static JSONArray getAverageRegionImport(String food, String region, String start, String end) {		
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		Double AvgImport = 0.0;
		Double AvgPrecipitation = 0.0;
		Double AvgTemperature = 0.0;
		Double year_selected = (Double.parseDouble(end) - Double.parseDouble(start)) + 1.0;
		
		MongoCursor<Document> cursor;
		Bson groupFilters;
					
		if(!region.equals("World")) {
			groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
		}
		else 
			groupFilters = Filters.and(Filters.eq("name", food));
		
		cursor = collection.aggregate(
			      Arrays.asList(
			    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			              Aggregates.match(Filters.and(groupFilters, 
			            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
			            		  Filters.lte("countries.years.year", Integer.parseInt(end))
			            		  ))
			      )
		).iterator();
		
		JSONArray result = new JSONArray();
		JSONObject country_result = new JSONObject();
		String prev_country = null;
		JSONObject obj;
		int prev = 0;
		int i = 0;
			
		try {
			while (cursor.hasNext()) {
				country_result = new JSONObject();
				Document document = null;
				obj = new JSONObject(cursor.next().toJson());
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = null;
				if(y.has("id_ie")) {
					ie = y.getJSONObject("id_ie");
					
					if(prev == 0) {
						prev_country = c.getString("country_name");
						prev = 1;
					}
					
					if(!prev_country.equals(c.getString("country_name"))) {
						if(AvgImport != 0.0) {
							country_result.put("AvgTemperature", AvgTemperature/year_selected);
							country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
							country_result.put("Import", AvgImport/year_selected);
							country_result.put("Country", prev_country);
						
							result.put(i, country_result);
							i++;
						}
						AvgTemperature = 0.0;
						AvgPrecipitation = 0.0;
						AvgImport = 0.0;

						prev_country = c.getString("country_name");
					}
					
					if(y.has("rainfall_avg"))
						if(!y.get("rainfall_avg").toString().equals("null"))
							AvgPrecipitation += Double.parseDouble(y.get("rainfall_avg").toString());
					
					if(y.has("temperature_avg"))
						if(!y.get("temperature_avg").toString().equals("null"))
							AvgTemperature += Double.parseDouble(y.get("temperature_avg").toString());
					
					document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
					if (document != null) {
						JSONObject d = new JSONObject(document.toJson());
						
						if(d.has("import_qty") && d.getInt("import_qty") != 0)
							AvgImport += Double.parseDouble(d.get("import_qty").toString());
					}
				}
			}
			if(AvgImport != 0.0) {
				country_result.put("AvgTemperature", AvgTemperature/year_selected);
				country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
				country_result.put("Import", AvgImport/year_selected);
				country_result.put("Country", prev_country);
				
				result.put(i, country_result);
			}
		} finally {
			cursor.close();
		}
		
		return result;
	}
	
	
	
	
	/*
	 * QUERY EXPORT
	 */
	public static JSONArray getTotalCountryExport(String food, String country, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		
		MongoCursor<Document> cursor;
						
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
				
		JSONArray result = new JSONArray();
		JSONObject obj;
		int i = 0;
			
		try {
			while (cursor.hasNext()) {
				JSONObject country_result = new JSONObject();
				obj = new JSONObject(cursor.next().toJson());
				
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = null;
				if(y.has("id_ie")) {
					ie = y.getJSONObject("id_ie");
					
					Document document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
					if (document != null) {
						JSONObject d = new JSONObject(document.toJson());
						if(d.has("export_qty") && d.getInt("export_qty") != 0) {
							System.out.println(d.getInt("export_qty"));
							country_result.put("Export", d.get("export_qty"));
							
							if(y.has("temperature_avg"))
								if(y.get("temperature_avg").toString().equals("null"))
									country_result.put("AvgTemperature", 0.0);
								else
									country_result.put("AvgTemperature", y.get("temperature_avg"));
							
							if(y.has("rainfall_avg"))
								if(y.get("rainfall_avg").toString().equals("null"))
									country_result.put("AvgPrecipitation", 0.0);
								else
									country_result.put("AvgPrecipitation", y.get("rainfall_avg"));
							country_result.put("Year", y.getInt("year"));
							country_result.put("Country", c.getString("country_name"));
							
							result.put(i, country_result);
							i++;
						}
					}
				}
			}
		} finally {
			cursor.close();
		}
		
		return result;
	}
	
	public static JSONArray getTotalRegionExport(String food, String region, String start, String end, boolean top5) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		Double TotalExport = 0.0;
		Double AvgPrecipitation = 0.0;
		Double AvgTemperature = 0.0;
		Double year_selected = (Double.parseDouble(end) - Double.parseDouble(start)) + 1.0;
		
		MongoCursor<Document> cursor;
		Bson groupFilters;
					
		if(!region.equals("World")) {
			groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
		}
		else 
			groupFilters = Filters.and(Filters.eq("name", food));
		
		cursor = collection.aggregate(
			      Arrays.asList(
			    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			              Aggregates.match(Filters.and(groupFilters, 
			            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
			            		  Filters.lte("countries.years.year", Integer.parseInt(end))
			            		  ))
			      )
		).iterator();
		
		JSONArray result = new JSONArray();
		JSONObject country_result = new JSONObject();
		String prev_country = null;
		JSONObject obj;
		int prev = 0;
		int i = 0;
			
		try {
			while (cursor.hasNext()) {
				country_result = new JSONObject();
				Document document = null;
				obj = new JSONObject(cursor.next().toJson());
				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = null;
				if(y.has("id_ie")) {
					ie = y.getJSONObject("id_ie");
					
					if(prev == 0) {
						prev_country = c.getString("country_name");
						prev = 1;
					}
					
					if(!prev_country.equals(c.getString("country_name"))) {
						if(TotalExport != 0.0) {
							country_result.put("AvgTemperature", AvgTemperature/year_selected);
							country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
							country_result.put("Export", TotalExport);
							country_result.put("Country", prev_country);
							
							result.put(i, country_result);
							i++;
						}
						AvgTemperature = 0.0;
						AvgPrecipitation = 0.0;
						TotalExport = 0.0;

						prev_country = c.getString("country_name");
					}
					
					if(y.has("rainfall_avg"))
						if(!y.get("rainfall_avg").toString().equals("null"))
							AvgPrecipitation += Double.parseDouble(y.get("rainfall_avg").toString());
					
					if(y.has("temperature_avg"))
						if(!y.get("temperature_avg").toString().equals("null"))
							AvgTemperature += Double.parseDouble(y.get("temperature_avg").toString());
					
					document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
					if (document != null) {
						JSONObject d = new JSONObject(document.toJson());
						
						if(d.has("export_qty") && d.getInt("export_qty") != 0)
							TotalExport +=  Double.parseDouble(d.get("export_qty").toString());
					}
				}
			}
			if(TotalExport != 0.0) {
				country_result.put("AvgTemperature", AvgTemperature/year_selected);
				country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
				country_result.put("Export", TotalExport);
				country_result.put("Country", prev_country);
				
				result.put(i, country_result);
			}
		} finally {
			cursor.close();
		}
		
		//for calculate the top5
		JSONArray sortedJsonArray = new JSONArray();
		if(top5) {    

		    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		    for (int j = 0; j < result.length(); j++) {
		        jsonValues.add(result.getJSONObject(j));
		    }
		    Collections.sort(jsonValues, new Comparator<JSONObject>() {
		        @Override
		        public int compare(JSONObject a, JSONObject b) {
		        	int compare = 0;
		            int valA = a.getInt("Export");
		            int valB = b.getInt("Export");
	                compare = Integer.compare(valB, valA);
		            return compare;
		        }
		    });
		    
		    int length = 5;
		    if(result.length() < 5) {
		    	length = result.length();
		    }
		    
		    for (int j = 0; j < length; j++) {
		        sortedJsonArray.put(j, jsonValues.get(j));
		    }
		    
		    result = sortedJsonArray;
		}
		
		return result;
	}
	
	public static JSONArray getAverageRegionExport(String food, String region, String start, String end) {
		collection = db.getCollection("dataModelArrAvg");
		ie_collection = db.getCollection("impExpInfo");
		Double AvgExport = 0.0;
		Double AvgPrecipitation = 0.0;
		Double AvgTemperature = 0.0;
		Double year_selected = (Double.parseDouble(end) - Double.parseDouble(start)) + 1.0;
		
		MongoCursor<Document> cursor;
		Bson groupFilters;
					
		if(!region.equals("World")) {
			groupFilters = Filters.and(Filters.eq("name", food), Filters.eq("countries.country_region",region));
		}
		else 
			groupFilters = Filters.and(Filters.eq("name", food));
		
		cursor = collection.aggregate(
			      Arrays.asList(
			    		  Aggregates.unwind("$countries", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			    		  Aggregates.unwind("$countries.years", new UnwindOptions().preserveNullAndEmptyArrays(true)),
			              Aggregates.match(Filters.and(groupFilters, 
			            		  Filters.gte("countries.years.year", Integer.parseInt(start)), 
			            		  Filters.lte("countries.years.year", Integer.parseInt(end))
			            		  ))
			      )
		).iterator();
		
		JSONArray result = new JSONArray();
		JSONObject country_result = new JSONObject();
		String prev_country = null;
		JSONObject obj;
		int prev = 0;
		int i = 0;
			
		try {
			while (cursor.hasNext()) {
				country_result = new JSONObject();
				Document document = null;
				obj = new JSONObject(cursor.next().toJson());

				JSONObject c = obj.getJSONObject("countries");
				JSONObject y = c.getJSONObject("years");
				JSONObject ie = null;
				if(y.has("id_ie")) {
					ie = y.getJSONObject("id_ie");
				
					if(prev == 0) {
						prev_country = c.getString("country_name");
						prev = 1;
					}
					
					if(!prev_country.equals(c.getString("country_name"))) {
						if(AvgExport != 0.0) {
							country_result.put("AvgTemperature", AvgTemperature/year_selected);
							country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
							country_result.put("Export", AvgExport/year_selected);
							country_result.put("Country", prev_country);
							
							result.put(i, country_result);
							i++;
						}
						AvgTemperature = 0.0;
						AvgPrecipitation = 0.0;
						AvgExport = 0.0;
	
						prev_country = c.getString("country_name");
					}
					
					
					if(y.has("rainfall_avg"))
						if(!y.get("rainfall_avg").toString().equals("null"))
							AvgPrecipitation += Double.parseDouble(y.get("rainfall_avg").toString());
					
					if(y.has("temperature_avg"))
						if(!y.get("temperature_avg").toString().equals("null"))
							AvgTemperature += Double.parseDouble(y.get("temperature_avg").toString());
					
					document = ie_collection.find(Filters.eq("_id", new ObjectId(ie.get("$oid").toString()))).first();
					if (document != null) {
						JSONObject d = new JSONObject(document.toJson());
						
						if(d.has("export_qty") && d.getInt("export_qty") != 0)
							AvgExport +=  Double.parseDouble(d.get("export_qty").toString());
					}
				}
			}
			if(AvgExport != 0.0) {
				country_result.put("AvgTemperature", AvgTemperature/year_selected);
				country_result.put("AvgPrecipitation", AvgPrecipitation/year_selected);
				country_result.put("Import", AvgExport/year_selected);
				country_result.put("Country", prev_country);
			}
			
		} finally {
			cursor.close();
		}
		
		return result;
	}
	
}
