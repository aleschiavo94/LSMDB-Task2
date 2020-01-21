import org.json.JSONArray;
import org.json.JSONObject;

public class YearlyWeatherFeatures {
	private double[] temperature;
	private double [] rainfall;
	
	public YearlyWeatherFeatures() {
		temperature = new double[12];
		rainfall = new double[12];
	}
	
	public void setTemperatureValue(double value, int month) {
		temperature[month] = value;
	}
	public void setRainfallValue(double value, int month) {
		rainfall[month] = value;
	}
	public double[] getTemperature() {
		return temperature;
	}
	public double[] getRainfall() {
		return rainfall;
	}
	public double getSpecificTemperature(int month) {
		return temperature[month];
	}
	public double getSpecificRainfall(int month) {
		return rainfall[month];
	}
	
	public YearlyWeatherFeatures(YearlyWeatherFeatures f) {
		temperature = new double[12];
		rainfall = new double[12];
		
		for(int i = 0; i < 12; i++) {
			temperature[i] = f.getSpecificTemperature(i);
			rainfall[i] = f.getSpecificRainfall(i);
			//System.out.println("valore a mese "+ i + " value: " +temperature[i]);
		}
		
		
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		
		JSONArray temp = new JSONArray();
		JSONArray rain = new JSONArray();
		
		JSONObject tmp = new JSONObject();
		
		for(int i = 0; i < 12; i++) {
			tmp.put(Integer.toString(i), this.getSpecificRainfall(i));
			rain.put(tmp);
			tmp = new JSONObject();
			tmp.put(Integer.toString(i), this.getSpecificTemperature(i));
			temp.put(tmp);
			tmp = new JSONObject();
		}
		json.put("temperature", temp);
		json.put("rainfall", rain);
		return json;
	}
	
	public String TemperatureToString() {
		String s = "";
		for(int i = 0; i < 12; i++) {
			s += Double.toString(this.temperature[i]) + "_";
		}
		
		return s;
	}
	public String RainToString() {
		String s = "";
		for(int i = 0; i < 12; i++) {
			s += Double.toString(this.rainfall[i]) + "_";
		}
		return s;
	}
	
	public boolean checkNotZeros() {
		for(int i = 0; i < 12; i++) {
			if(this.temperature[i] == 0.0 || this.rainfall[i] == 0.0)
				return false;
		}
		return true;
	}
}
