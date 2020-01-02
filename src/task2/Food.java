package task2;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Food {
	String name;
	String seasonality;
	List<CountryInfo> countries;
	
	
	public Food(String name, String seasonality) {
		this.name = name;
		this.seasonality = seasonality;
		countries = new ArrayList<>();
	}
	
	public void addCountry(CountryInfo c) {
		countries.add(c);
	}
	
	public String getName() {
		return name;
	}
	public String getSeasonality() {
		return seasonality;
	}
	public List<CountryInfo> getCountries(){
		return countries;
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		
		json.put("name", name);
		json.put("seasonality", seasonality);
		
		JSONArray arrCountries = new JSONArray();
		JSONObject country = new JSONObject();
		//System.out.println("country size: " + this.countries.size());
		for(int i = 0; i < countries.size(); i++) {
			
			country = countries.get(i).toJson();
			arrCountries.put(country);
			country = new JSONObject();
		}
		
		json.put("countries", arrCountries);
		return json;
	}
	
}
