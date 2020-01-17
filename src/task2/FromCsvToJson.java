package task2;
import org.json.JSONObject;

public class FromCsvToJson {
	private String country_name;
	private String name;
	private String year;
	private String production;
	private String temperature_avg;
	private String rainfall_avg;
	private String temperature;
	private String rain;

	public FromCsvToJson(String[] csv) {
		name = csv[0];
		country_name = csv[1];
		year = csv[2];
		rain = csv[3];
		production = csv[4];
		temperature_avg = csv[5];
		temperature = csv[6];
		rainfall_avg = csv[7];
	}
	
	public FromCsvToJson(JSONObject json) {
		this.name = json.getString("name");
		this.country_name = json.getString("country_name");
		this.year = json.getString("year");
		this.rain = json.getString("rain");
		this.production = json.getString("production");
		this.temperature_avg = json.getString("temperature_avg");
		this.temperature = json.getString("temperature");
		this.rainfall_avg = json.getString("rainfall_avg");
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("country_name", country_name);
		json.put("year", year);
		json.put("rain", rain);
		json.put("production", production);
		json.put("temperature_avg", temperature_avg);
		json.put("temperature", temperature);
		json.put("rainfall_avg", rainfall_avg);			
		return json;
	}
	
	public String getName() {
		return this.name;
	}
	public String getCountry() {
		return this.country_name;
	}
	public String getYear() {
		return this.year;
	}
	public String getRain() {
		return this.rain;
	}
	public String getProduction() {
		return this.production;
	}
	public String getTemperatureAvg() {
		return this.temperature_avg;
	}
	public String getTemperature() {
		return this.temperature;
	}
	public String getRainfallAvg() {
		return this.rainfall_avg;
	}
}
