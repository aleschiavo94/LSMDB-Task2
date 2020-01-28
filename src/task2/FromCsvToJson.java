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
	private String import_qty;
	private String import_value;
	private String export_qty;
	private String export_value;

	public FromCsvToJson(String[] csv) {
		name = csv[0];
		country_name = csv[1];
		year = csv[2];
		rain = csv[3];
		production = csv[4];
		temperature_avg = csv[5];
		temperature = csv[6];
		rainfall_avg = csv[7];
		import_qty = csv[8];
		import_value = csv[9];
		export_qty = csv[10];
		export_value = csv[11];
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
		this.import_qty = json.getString("import_qty");
		this.import_value = json.getString("import_value");
		this.export_qty = json.getString("export_qty");
		this.export_value = json.getString("export_value");
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
		json.put("import_qty", import_qty);
		json.put("import_value", import_value);
		json.put("export_qty", export_qty);
		json.put("export_value", export_value);
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
	public String getImportQty() {
		return this.import_qty;
	}
	public String getImportValue() {
		return this.import_value;
	}
	public String getExportQty() {
		return this.export_qty;
	}
	public String getExportValue() {
		return this.export_value;
	}
}
