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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class ResultController implements Initializable {
	
	private JSONArray result = new JSONArray();

	
	@FXML private BarChart<String, Number>  parameterChart;
	@FXML private LineChart tempLineChart;
	@FXML private LineChart rainLineChart;
	@FXML private BarChart<String, Number> tempBarChart;
	@FXML private BarChart<String, Number> rainBarChart;
	
	@FXML private CategoryAxis param_yearX;
	@FXML private NumberAxis param_valueY;
	
	
	@FXML private CategoryAxis temp_yearX;
	@FXML private NumberAxis temp_valueY;
	
	@FXML private CategoryAxis rain_yearX;
	@FXML private NumberAxis rain_valueY;
	
	@FXML private Label parameterLabel;
	
	@FXML private Label objectiveLabel;
	@FXML private Label resultLabel;
	@FXML private Label title;
	
	@FXML private PieChart pieChart;
	@FXML private Label pieChartLabel;
	private boolean top5;
	
	private List<ResultSearchObject> results;
	
	public void initResult(JSONArray result, String region, String country, String aim, String aggregation, String pLabel, String oLabel, boolean top5, String food, String start, String end) {
		JSONArray pointer = null;
		this.top5 = top5;
		
		if(region == null || country != null)
			this.title.setText(food +" " + aim + " in " + country + " from "+ start + " to " + end);
		else
			this.title.setText(food +" " + aim + " in " + region + " from " + start + " to " + end);
		
		pointer = result;
		
		this.parameterLabel.setText(pLabel);
		this.objectiveLabel.setText(oLabel);
		
		if(pointer != null) {
			JSONObject json = new JSONObject();
			this.results = new ArrayList<>();
			for(int i = 0; i < pointer.length(); i++) {
				json = pointer.getJSONObject(i);
				this.results.add(new ResultSearchObject(json));
			}
			if(!top5) {
				Collections.sort(this.results);
			}
				int res = 0;
				if(aggregation.contentEquals("Average")) {
					for(int i = 0; i < this.results.size(); i++) {
						res += this.results.get(i).getParameterSought();
					}
					res = res / this.results.size();
					this.resultLabel.setText(Integer.toString(res)+" tonnes");
				}
				else if(aggregation.contentEquals("Sum") || aggregation.contentEquals("Top 5")) {
					for(int i = 0; i < results.size(); i++) {
						System.out.println(this.results.get(i).getParameterSought());
						res += this.results.get(i).getParameterSought();
					}
					this.resultLabel.setText(Integer.toString(res)+" tonnes");
				}
			setPlots(country);
		}
		
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	
	//Format parameter and insert in Charts
	public void setPlots(String country) {
//		this.pieChart.setLabelsVisible(false);
//		this.pieChart.setLabelLineLength(0);

		ResultSearchObject pointer = null;
		
		if(!top5 && country != null) {
			this.pieChart.setVisible(false);
			this.rainBarChart.setVisible(false);
			this.tempBarChart.setVisible(false);
			
			XYChart.Series<String, Number> paramSeries = new XYChart.Series();
			paramSeries.setName(this.results.get(0).getCountry()); 
	        
			XYChart.Series<String, Number> tempSeries = new XYChart.Series();
			tempSeries.setName(this.results.get(0).getCountry()); 
	        
			XYChart.Series<String, Number> rainSeries = new XYChart.Series();
			rainSeries.setName(this.results.get(0).getCountry()); 
			for(int i = 0; i < this.results.size(); i++) {
				pointer = this.results.get(i);
				paramSeries.getData().add(new XYChart.Data(Integer.toString(pointer.getYear()), pointer.getParameterSought()));
				tempSeries.getData().add(new XYChart.Data(Integer.toString(pointer.getYear()), pointer.getAvgTemp()));
				rainSeries.getData().add(new XYChart.Data(Integer.toString(pointer.getYear()), pointer.getAvgRain()));
			}
			this.parameterChart.getData().add(paramSeries);
			this.rainLineChart.getData().add(rainSeries);
			this.tempLineChart.getData().add(tempSeries);

	        
			this.parameterChart.setLegendVisible(false);
			this.rainLineChart.setLegendVisible(false);
			this.tempLineChart.setLegendVisible(false);
		}else{

			setPlotsTop5();
		}

        
       
	}
	
	public void setPlotsTop5() {
//		objectiveLabel.setVisible(false);
//		resultLabel.setVisible(false);
		this.rainLineChart.setVisible(false);
		this.tempLineChart.setVisible(false);
		
		ResultSearchObject pointer = null;
		ObservableList<PieChart.Data> valueList = FXCollections.observableArrayList();
		for(int i = 0; i < this.results.size(); i++) {
			pointer = this.results.get(i);
			valueList.add(new PieChart.Data(pointer.getCountry(), pointer.getParameterSought()));
		}
		this.pieChart.setData(valueList);
		
		pointer = null;
		for(int i = 0 ; i < this.results.size(); i++) {
			pointer = this.results.get(i);
			XYChart.Series<String, Number> paramSeries = new XYChart.Series();
			paramSeries.setName(pointer.getCountry()); 
			XYChart.Series<String, Number> tempSeries = new XYChart.Series();
			tempSeries.setName(pointer.getCountry());
			XYChart.Series<String, Number> rainSeries = new XYChart.Series();
			rainSeries.setName(pointer.getCountry()); 
			
			paramSeries.getData().add(new XYChart.Data(pointer.getCountry(), pointer.getParameterSought()));
			tempSeries.getData().add(new XYChart.Data(pointer.getCountry(), pointer.getAvgTemp()));
			rainSeries.getData().add(new XYChart.Data(pointer.getCountry(), pointer.getAvgRain()));
			
			this.parameterChart.getData().add(paramSeries);
			this.rainBarChart.getData().add(rainSeries);
			this.tempBarChart.getData().add(tempSeries);
	        
			this.parameterChart.setLegendVisible(false);
			this.rainBarChart.setLegendVisible(false);
			this.tempBarChart.setLegendVisible(false);
		}
	}
}