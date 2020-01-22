package task2;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.fxml.Initializable;

public class ResultController implements Initializable {
	
	private JSONObject TotalProduction = new JSONObject();
	private JSONObject AvgProduction = new JSONObject();
//	private JSONObject top5Production = null;
	private JSONObject TotalImport = new JSONObject();
	private JSONObject AvgImport = new JSONObject();
	private JSONObject TotalExport = new JSONObject();
	private JSONObject AvgExport = new JSONObject();

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
				AvgProduction = MongoHandler.getTop5Production(food,region,start,end);
				System.out.println(AvgProduction);
			}
		}
		else if(aim.equals("Import")) {
			if(aggregation.equals("Sum")) {
				TotalImport = MongoHandler.getTotalImport(food,region,country,start,end);
				System.out.println(TotalImport);
			}
			else if(aggregation.contentEquals("Average")) {
				AvgImport = MongoHandler.getAverageImport(food,region,country,start,end);
				System.out.println(AvgImport);
			}
			else if(aggregation.contentEquals("Top 5")) {
				AvgProduction = MongoHandler.getAverageProduction(food,region,country,start,end);
				System.out.println(AvgProduction);
			}
		}
		else if(aim.equals("Export")) {
			if(aggregation.equals("Sum")) {
				TotalExport = MongoHandler.getTotalExport(food,region,country,start,end);
				System.out.println(TotalExport);
			}
			else if(aggregation.contentEquals("Average")) {
				AvgExport = MongoHandler.getAverageExport(food,region,country,start,end);
				System.out.println(AvgExport);
			}
			else if(aggregation.contentEquals("Top 5")) {
				AvgProduction = MongoHandler.getAverageProduction(food,region,country,start,end);
				System.out.println(AvgProduction);
			}
		}
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
	
	}
}
