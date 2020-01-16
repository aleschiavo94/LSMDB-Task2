package task2;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class ResultController implements Initializable {
	
	private int TotalProduction = 0;
	private Double AvgProduction = 0.0;
	private int TotalImport = 0;
	private Double AvgImport = 0.0;
	private int TotalExport = 0;
	private Double AvgExport = 0.0;

	public void initResult(String food, String region, String country, String aim, String start, String end, String aggregation) {
		if(aim.equals("Production")) {
			if(aggregation.equals("Sum")) {
				TotalProduction = MongoHandler.getTotalProduction(food,region,country,start,end);
				System.out.println(TotalProduction);
			}
			
			if(aggregation.contentEquals("Average")) {
				AvgProduction = MongoHandler.getAverageProduction(food,region,country,start,end);
				System.out.println(AvgProduction);
			}
		}
		else if(aim.equals("Import")) {
			if(aggregation.equals("Sum")) {
				TotalImport = MongoHandler.getTotalImport(food,region,country,start,end);
				System.out.println(TotalImport);
			}
			
			if(aggregation.contentEquals("Average")) {
				AvgImport = MongoHandler.getAverageProduction(food,region,country,start,end);
				System.out.println(AvgProduction);
			}
		}
		else if(aim.equals("Export")) {
			if(aggregation.equals("Sum")) {
				TotalExport = MongoHandler.getTotalProduction(food,region,country,start,end);
				System.out.println(TotalProduction);
			}
			
			if(aggregation.contentEquals("Average")) {
				AvgExport = MongoHandler.getAverageProduction(food,region,country,start,end);
				System.out.println(AvgProduction);
			}
		}
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
	
	}
}
