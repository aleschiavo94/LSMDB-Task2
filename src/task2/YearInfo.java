package task2;

import org.json.JSONArray;
import org.json.JSONObject;

public class YearInfo {
	int year;
	double production_qty;
	YearlyWeatherFeatures features;
	//Trade tradeLink;
	
	public YearInfo(int year, double prod_qty, YearlyWeatherFeatures features) {
		
		this.year = year;
		this.production_qty = prod_qty;
		// GUARDA COME COPIARE ARRAY INTO EACH OTHER 
		//System.out.println("entra");
		this.features = new YearlyWeatherFeatures(features);
		//tradeLink = null;
	}
	
	public YearInfo(int year, double prod_qty) {
		this.year  = year;
		this.production_qty = prod_qty;
		this.features = null;
	}
	
	public int getYear() {
		return year;
	}
	public double getProduction() {
		return production_qty;
	}
	public YearlyWeatherFeatures getWeatherFeatures() {
		return features;
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		if(production_qty <= 0 && features != null && !features.checkNotZeros())
			return json;
		
		json.put("year", year);
		if(production_qty > 0) {
			json.put("production", this.production_qty);
		}
		if(features != null) {
			if(!features.checkNotZeros())
				return json;
			JSONArray temp = new JSONArray();
			JSONArray rain = new JSONArray();
			
			JSONObject tmp = new JSONObject();
			
			/*for(int i = 0; i < 12; i++) {
				tmp.put(Integer.toString(i), features.getSpecificRainfall(i));
				rain.put(tmp);
				tmp = new JSONObject();
				tmp.put(Integer.toString(i), features.getSpecificTemperature(i));
				temp.put(tmp);
				tmp = new JSONObject();
			}
			json.put("temperature", temp);
			json.put("rainfall", rain);*/
			json.put("temperature", features.TemperatureToString());
			json.put("rain", features.RainToString());
			return json;
		}
		
		return null;
	}
	
}
