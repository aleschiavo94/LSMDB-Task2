package task2;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodList {
	List<String> list_food;
	
	
	public FoodList() {
		this.list_food.addAll(MongoHandler.getFood());
	}
	
	List<String> getFood(){
		return this.list_food;
	}
}
