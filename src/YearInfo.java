import org.json.JSONArray;
import org.json.JSONObject;

public class YearInfo {
	int year;
	double production_qty;
	YearlyWeatherFeatures features;
	Trade tradeLink;
	
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
			
			
			json.put("temperature", features.TemperatureToString());
			json.put("rain", features.RainToString());
			return json;
		}
		
		return null;
	}//toJson
	
	public YearInfo(JSONObject json) {
		
		this.year = json.getInt("year");
		if(json.has("production"))
			this.production_qty = json.getDouble("production");
		int i = 0;
		YearlyWeatherFeatures weather = new YearlyWeatherFeatures();
		if(json.has("temperature")) {
			String temp = json.getString("temperature");
			String[] temp_split = temp.split("_");
			while(i < 12 || i < temp_split.length) {
				weather.setTemperatureValue(Double.parseDouble(temp_split[i]), i);
				i++;
			}
		}
		i = 0;
		if(json.has("rain")) {
			String rain = json.getString("rain");
			String[] rain_split = rain.split("_");
			while(i < 12 || i < rain_split.length) {
				weather.setRainfallValue(Double.parseDouble(rain_split[i]), i);
				i++;
			}
		}
	}//fromJson
	
}
