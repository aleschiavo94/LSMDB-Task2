package task2;

import org.json.JSONObject;

public class ResultSearchObject {
	private String country;
	private double avgTemp;
	private double avgRain;
	private int year;
	private int parameterSought;
	
	public ResultSearchObject(JSONObject json) {
		
		country = json.getString("Country");
		avgTemp = json.getDouble("AvgTemperature");
		avgRain = json.getDouble("AvgPrecipitation");
		year = json.getInt("Year");
		
		if(json.has("TotalProduction"))
			parameterSought = json.getInt("TotalProduction");
		if(json.has("Import"))
			parameterSought = json.getInt("Import");
		if(json.has("Export"))
			parameterSought = json.getInt("Export");
		
	}
	
}
