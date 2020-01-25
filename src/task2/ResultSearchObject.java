package task2;

import org.json.JSONObject;

public class ResultSearchObject implements Comparable<ResultSearchObject> {
	private String country;
	private double avgTemp;
	private double avgRain;
	private int year;
	private int parameterSought;
	public ResultSearchObject(JSONObject json) {
		
		country = json.getString("Country");
		avgTemp = json.getDouble("AvgTemperature");
		avgRain = json.getDouble("AvgPrecipitation");
		if(json.has("Year"))
			year = json.getInt("Year");
		
		if(json.has("TotalProduction"))
			parameterSought = json.getInt("TotalProduction");
		if(json.has("Import"))
			parameterSought = json.getInt("Import");
		if(json.has("Export"))
			parameterSought = json.getInt("Export");
		if(json.has("AvgProduction"))
			parameterSought = json.getInt("AvgProduction");
		
	}

	public String getCountry() {
		return country;
	}

	public double getAvgTemp() {
		return avgTemp;
	}

	public double getAvgRain() {
		return avgRain;
	}

	public int getYear() {
		return year;
	}

	public int getParameterSought() {
		return parameterSought;
	}
	
	@Override
	public int compareTo(ResultSearchObject r) {
		return this.year - r.getYear();
	}
}
