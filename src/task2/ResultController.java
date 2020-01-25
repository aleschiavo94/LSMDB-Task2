package task2;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class ResultController implements Initializable {
	
	private JSONArray TotalProduction = new JSONArray();
	private JSONArray AvgProduction = new JSONArray();
	private JSONArray top5Production =new JSONArray();
	private JSONArray TotalImport = new JSONArray();
	private JSONArray AvgImport = new JSONArray();
	private JSONArray TotalExport = new JSONArray();
	private JSONArray AvgExport = new JSONArray();

	
	@FXML private BarChart<String, Number>  parameterChart;
	@FXML private BarChart<String, Number> tempChart;
	@FXML private BarChart<String, Number> rainChart;
	
	@FXML private CategoryAxis param_yearX;
	@FXML private NumberAxis param_valueY;
	
	
	@FXML private CategoryAxis temp_yearX;
	@FXML private NumberAxis temp_valueY;
	
	@FXML private CategoryAxis rain_yearX;
	@FXML private NumberAxis rain_valueY;
	
	@FXML private Label parameterLabel;
	
	@FXML private Label objectiveLabel;
	@FXML private Label resultLabel;
	
	@FXML private PieChart pieChart;
	private boolean top5;
	
	private List<ResultSearchObject> results;
	
	public void initResult(String food, String region, String country, String aim, String start, String end, String aggregation) {
		JSONArray pointer = null;
		top5 = false;
		if(aim.equals("Production")) {
			if(aggregation.equals("Sum")) {
				TotalProduction = MongoHandler.getTotalProduction(food,region,country,start,end);
				System.out.println(TotalProduction);
				pointer = TotalProduction;		
				parameterLabel.setText("Total Production (tonnes)");
				objectiveLabel.setText("Total production Value: ");
			}
			else if(aggregation.contentEquals("Average")) {
				AvgProduction = MongoHandler.getAverageProduction(food,region,country,start,end);
				System.out.println(AvgProduction);
				pointer = AvgProduction;
				parameterLabel.setText("AVG Production (tonnes)");
				objectiveLabel.setText("Avg Yearly Production: ");
			}
			else if(aggregation.contentEquals("Top 5")) {
				top5 = true;
				top5Production = MongoHandler.getTop5Production(food,region,start,end);
				System.out.println(top5Production);
				pointer = top5Production;
				parameterLabel.setText("Top 5 Production (tonnes)");
				objectiveLabel.setText("Total Production Value: ");
			}
		}
		else if(aim.equals("Import")) {
			if(aggregation.equals("Sum")) {
				if(region != null)
					TotalImport = MongoHandler.getTotalRegionImport(food,region,start,end);
				else
					TotalImport = MongoHandler.getTotalCountryImport(food,country,start,end);
				System.out.println(TotalImport);
				pointer = TotalImport;
				parameterLabel.setText("Total Import (tonnes)");
				objectiveLabel.setText("Total Import Value: ");
			}
			else if(aggregation.contentEquals("Average")) {
				if(region != null)
					AvgImport = MongoHandler.getAverageRegionImport(food,region,start,end);
				else
					AvgImport = MongoHandler.getTotalCountryImport(food,country,start,end);
				System.out.println(AvgImport);
				pointer =AvgImport;
				parameterLabel.setText("AVG Import (tonnes)");
				objectiveLabel.setText("Avg Yearly Import: ");
			}
		}
		else if(aim.equals("Export")) {
			if(aggregation.equals("Sum")) {
				if(region != null)
					TotalExport = MongoHandler.getTotalRegionExport(food,region,start,end);
				else
					TotalExport = MongoHandler.getTotalCountryExport(food,country,start,end);
				System.out.println(TotalExport);
				pointer = TotalExport;
				parameterLabel.setText("Total Export (tonnes)");
				objectiveLabel.setText("Total Export Value: ");
			}
			else if(aggregation.contentEquals("Average")) {
				if(region != null)
					AvgExport = MongoHandler.getAverageRegionExport(food,region,start,end);
				else
					AvgExport = MongoHandler.getTotalCountryExport(food,country,start,end);
				System.out.println(AvgExport);
				pointer = AvgExport;
				parameterLabel.setText("AVG Export (tonnes)");
				objectiveLabel.setText("Avg Yearly Export: ");
			}
		}
		
		
		
		if(pointer != null) {
			JSONObject json = new JSONObject();
			results = new ArrayList<>();
			for(int i = 0; i < pointer.length(); i++) {
				json = pointer.getJSONObject(i);
				results.add(new ResultSearchObject(json));
				
			}
			if(!top5) {
				Collections.sort(results);
			}
				int res = 0;
				if(aggregation.contentEquals("Average")) {
					for(int i = 0; i < results.size(); i++)
						res += results.get(i).getParameterSought();
					res = res / results.size();
					resultLabel.setText(Integer.toString(res));
				}else if(aggregation.contentEquals("Sum") || aggregation.contentEquals("Top 5")) {
					for(int i = 0; i < results.size(); i++)
						res += results.get(i).getParameterSought();
					resultLabel.setText(Integer.toString(res));
				}
			setPlots();
		}
		
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	//Format parameter and insert in Charts
	public void setPlots() {

		ResultSearchObject pointer = null;
		
		if(!top5) {
			pieChart.setVisible(false);
			XYChart.Series<String, Number> paramSeries = new XYChart.Series();
			paramSeries.setName(results.get(0).getCountry()); 
	        
			XYChart.Series<String, Number> tempSeries = new XYChart.Series();
			tempSeries.setName(results.get(0).getCountry()); 
	        
			XYChart.Series<String, Number> rainSeries = new XYChart.Series();
			rainSeries.setName(results.get(0).getCountry()); 
			for(int i = 0; i < results.size(); i++) {
				pointer = results.get(i);
				paramSeries.getData().add(new XYChart.Data(Integer.toString(pointer.getYear()), pointer.getParameterSought()));
				tempSeries.getData().add(new XYChart.Data(Integer.toString(pointer.getYear()), pointer.getAvgTemp()));
				rainSeries.getData().add(new XYChart.Data(Integer.toString(pointer.getYear()), pointer.getAvgRain()));
			}
	        parameterChart.getData().add(paramSeries);
	        rainChart.getData().add(rainSeries);
	        tempChart.getData().add(tempSeries);
		}else {
			
			setPlotsTop5();
		}

        
       
	}
	
	public void setPlotsTop5() {
		ResultSearchObject pointer = null;
		ObservableList<PieChart.Data> valueList = FXCollections.observableArrayList();
		for(int i = 0; i < results.size(); i++) {
			pointer = results.get(i);
			valueList.add(new PieChart.Data(pointer.getCountry(), pointer.getParameterSought()));
		}
		pieChart.setData(valueList);
		
		pointer = null;
		for(int i = 0 ; i < results.size(); i++) {
			pointer = results.get(i);
			XYChart.Series<String, Number> paramSeries = new XYChart.Series();
			paramSeries.setName(pointer.getCountry()); 
			XYChart.Series<String, Number> tempSeries = new XYChart.Series();
			tempSeries.setName(pointer.getCountry());
			XYChart.Series<String, Number> rainSeries = new XYChart.Series();
			rainSeries.setName(pointer.getCountry()); 
			
			paramSeries.getData().add(new XYChart.Data(pointer.getCountry(), pointer.getParameterSought()));
			tempSeries.getData().add(new XYChart.Data(pointer.getCountry(), pointer.getAvgTemp()));
			rainSeries.getData().add(new XYChart.Data(pointer.getCountry(), pointer.getAvgRain()));
			
			parameterChart.getData().add(paramSeries);
	        rainChart.getData().add(rainSeries);
	        tempChart.getData().add(tempSeries);
		}
	}
}
