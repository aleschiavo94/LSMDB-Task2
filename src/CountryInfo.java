import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryInfo {
	String name;
	String region;
	HashMap<Integer, YearInfo> years;
	
	public CountryInfo(String name, String region) {
		this.name = name;
		this.region = region;
		years = new HashMap<Integer, YearInfo>();
	}
	
	public void addYear(YearInfo y) {
		years.put(y.getYear(), y);
	}
	
	public String getName() {
		return name;
	}
	public String getRegion() {
		return region;
	}
	public HashMap<Integer, YearInfo> getYears(){
		return years;
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		
		json.put("country_name", name);
		json.put("country_region", region);
		
		
		JSONArray arrYears = new JSONArray();
		JSONObject year = new JSONObject();
		int y = 1961;
		while(y < 2015) {
			if(years.containsKey(y)) {
				year = years.get(y).toJson();
				if(year != null) {
					arrYears.put(year);
					year = new JSONObject();
				}
				y++;
			}
		}
		json.put("years", arrYears);
		
		return json;
	}
	
}
