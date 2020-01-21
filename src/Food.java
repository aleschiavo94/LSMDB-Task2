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
	
	public Food(JSONObject json) {
		this.name = json.getString("name");
		this.seasonality = json.getString("seasonality");
		
		this.countries = new ArrayList<>();
		JSONArray array = json.getJSONArray("countries");
		for(int i = 0; i < array.length(); i++) {
			
			JSONObject pointer = array.getJSONObject(i);
			//System.out.println("while food " + name + " " + pointer.getString("country_name"));
			
			CountryInfo ci = new CountryInfo(pointer.getString("country_name"), pointer.getString("country_region"));
			JSONArray array_year = pointer.getJSONArray("years");
			for(int c = 0; c < array_year.length(); c++) {
				//System.out.println("while year " + array_year.length() + " - " + c);
				JSONObject single_year = array_year.getJSONObject(c);
				if(!single_year.has("year"))
					continue;
				YearInfo tmp = new YearInfo(single_year);
				ci.addYear(tmp);
			}
			countries.add(ci);
		}
		//System.out.println("extracted food");
	}
	
}
