package task2;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.Initializable;

public class ResultController implements Initializable {
	
	private JSONArray TotalProduction = new JSONArray();
	private JSONArray AvgProduction = new JSONArray();
	private JSONArray top5Production =new JSONArray();
	private JSONArray TotalImport = new JSONArray();
	private JSONArray AvgImport = new JSONArray();
	private JSONArray TotalExport = new JSONArray();
	private JSONArray AvgExport = new JSONArray();

	public void initResult(String food, String region, String country, String aim, String start, String end, String aggregation) {
		if(aim.equals("Production")) {
			if(aggregation.equals("Sum")) {
				TotalProduction = MongoHandler.getTotalProduction(food,region,country,start,end);
				System.out.println(TotalProduction);
			}
			else if(aggregation.contentEquals("Average")) {
				AvgProduction = MongoHandler.getAverageProduction(food,region,country,start,end);
				System.out.println(AvgProduction);
			}
			else if(aggregation.contentEquals("Top 5")) {
				top5Production = MongoHandler.getTop5Production(food,region,start,end);
				System.out.println(top5Production);
			}
		}
		else if(aim.equals("Import")) {
			if(aggregation.equals("Sum")) {
				if(region != null)
					TotalImport = MongoHandler.getTotalRegionImport(food,region,start,end);
				else
					TotalImport = MongoHandler.getTotalCountryImport(food,country,start,end);
				System.out.println(TotalImport);
			}
			else if(aggregation.contentEquals("Average")) {
				if(region != null)
					AvgImport = MongoHandler.getAverageRegionImport(food,region,start,end);
				else
					AvgImport = MongoHandler.getTotalCountryImport(food,country,start,end);
				System.out.println(AvgImport);
			}
		}
		else if(aim.equals("Export")) {
			if(aggregation.equals("Sum")) {
				if(region != null)
					TotalExport = MongoHandler.getTotalRegionExport(food,region,start,end);
				else
					TotalExport = MongoHandler.getTotalCountryExport(food,country,start,end);
				System.out.println(TotalExport);
			}
			else if(aggregation.contentEquals("Average")) {
				if(region != null)
					AvgExport = MongoHandler.getAverageRegionExport(food,region,start,end);
				else
					AvgExport = MongoHandler.getTotalCountryExport(food,country,start,end);
				System.out.println(AvgExport);
			}
		}
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
	
	}
}
