package task2;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DocumentController implements Initializable {
	//country search
	@FXML private TextField search_field;
	
	//date fields
	@FXML private TextField start_date;
	@FXML private TextField end_date;
		
	//continent combobox
	@FXML private ComboBox<String> continent_comboBox;
		private ObservableList<String> continent_list;
		
	//food combobox
	@FXML private ComboBox<String> food_comboBox;
		private ObservableList<String> food_list;
	
	//aggregation combobox
	@FXML private ComboBox<String> aggregation;
		private ObservableList<String> aggregation_list;
		
	//radio button 
	@FXML RadioButton rb_production;
	@FXML RadioButton rb_import;
	@FXML RadioButton rb_export;
		private ToggleGroup group;
		
		private String food_selected = null;
		private String region_selected = null;
		private String radio_selected = null;
		private String start_year = null;
		private String end_year = null;
		private String search = null;
		private String aggregation_selected = null;
		
		private List<String> list_food;
			
	public void submit() throws IOException{
		//getting the values
		if(food_comboBox.getSelectionModel().isEmpty() || 
			(continent_comboBox.getSelectionModel().isEmpty() && search_field.getLength() == 0) ||
			aggregation.getSelectionModel().isEmpty() ||
			group.getSelectedToggle()==null|| 
			start_date.getLength() == 0 || end_date.getLength() == 0
			) {
        	
        	Alert windowAlert = new Alert(AlertType.INFORMATION);
        	windowAlert.setHeaderText(null);
			windowAlert.setContentText("Please fill all the fields");
			windowAlert.setTitle("Warning");
			windowAlert.showAndWait();
        	return;
		}
		else if(continent_comboBox.getSelectionModel().isEmpty() && aggregation.getSelectionModel().getSelectedItem().toString().equals("Top 5")) {
			Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText(null);
			windowAlert.setContentText("With Top 5 aggregation, please select a Region");
			windowAlert.setTitle("Warning");
			windowAlert.showAndWait();
			
			search_field.clear();
        	return;
		}
		else {
			food_selected = food_comboBox.getSelectionModel().getSelectedItem().toString();
			if(continent_comboBox.getSelectionModel().isEmpty()) 
				region_selected = null;
			else 
				region_selected = continent_comboBox.getSelectionModel().getSelectedItem().toString();
			RadioButton selected = (RadioButton)group.getSelectedToggle();
			radio_selected = selected.getText();
			start_year = start_date.getText();
			end_year = end_date.getText();
			if(search_field.getLength() > 0)
				search = search_field.getText();
			else
				search = null;
			aggregation_selected = aggregation.getSelectionModel().getSelectedItem().toString();
					
			//cleaning the fields
			food_comboBox.getSelectionModel().clearSelection();
			continent_comboBox.getSelectionModel().clearSelection();
			start_date.clear();
			end_date.clear();
			search_field.clear();
			aggregation.getSelectionModel().clearSelection();
			
			group.getToggles().clear();
			
			testSubmit();
		}
	}
	
	private void testSubmit() {
		JSONArray result = new JSONArray();
		boolean top5 = false;
		String parameterLabel = null;
		String objectiveLabel = null;
		
		if(radio_selected.equals("Production")) {
			if(aggregation_selected.equals("Sum")) {
				result = MongoHandler.getTotalProduction(food_selected,region_selected,search,start_year,end_year);
				System.out.println(result);		
				parameterLabel = "Total Production (tonnes)";
				objectiveLabel = "Total production Value: ";
			}
			else if(aggregation_selected.equals("Average")) {
				result = MongoHandler.getAverageProduction(food_selected,region_selected,search,start_year,end_year);
				System.out.println(result);
				parameterLabel = "AVG Production (tonnes)";
				objectiveLabel = "Avg Yearly Production: ";
			}
			else if(aggregation_selected.equals("Top 5")) {
				top5 = true;
				result = MongoHandler.getTop5Production(food_selected,region_selected,start_year,end_year);
				System.out.println(result);
				parameterLabel = "Top 5 Production (tonnes)";
				objectiveLabel = "Total Production Value: ";
			}
		}
		else if(radio_selected.equals("Import")) {
			if(aggregation_selected.equals("Sum")) {
				if(region_selected != null && search == null)
					result = MongoHandler.getTotalRegionImport(food_selected,region_selected,start_year,end_year,false);
				else
					result = MongoHandler.getTotalCountryImport(food_selected,search,start_year,end_year);
				System.out.println(result);
				parameterLabel = "Total Import (tonnes)";
				objectiveLabel = "Total Import Value: ";
			}
			else if(aggregation_selected.contentEquals("Average")) {
				if(region_selected != null && search == null)
					result = MongoHandler.getAverageRegionImport(food_selected,region_selected,start_year,end_year);
				else
					result = MongoHandler.getTotalCountryImport(food_selected,search,start_year,end_year);
				System.out.println(result);
				parameterLabel = "AVG Import (tonnes)";
				objectiveLabel = "Avg Yearly Import: ";
			}
			else if(aggregation_selected.contentEquals("Top 5")) {
				result = MongoHandler.getTotalRegionExport(food_selected,region_selected,start_year,end_year,true);
				parameterLabel = "Top 5 Import (tonnes)";
				objectiveLabel = "Total Import Value: ";
			}
		}
		else if(radio_selected.equals("Export")) {
			if(aggregation_selected.equals("Sum")) {
				if(region_selected != null && search == null)
					result = MongoHandler.getTotalRegionExport(food_selected,region_selected,start_year,end_year,false);
				else
					result = MongoHandler.getTotalCountryExport(food_selected,search,start_year,end_year);
				System.out.println(result);
				parameterLabel = "Total Export (tonnes)";
				objectiveLabel = "Total Export Value: ";
			}
			else if(aggregation_selected.contentEquals("Average")) {
				if(region_selected != null && search == null)
					result = MongoHandler.getAverageRegionExport(food_selected,region_selected,start_year,end_year);
				else
					result = MongoHandler.getTotalCountryExport(food_selected,search,start_year,end_year);
				System.out.println(result);
				parameterLabel = "AVG Export (tonnes)";
				objectiveLabel = "Avg Yearly Export: ";
			}
			else if(aggregation_selected.contentEquals("Top 5")) {
				result = MongoHandler.getTotalRegionExport(food_selected,region_selected,start_year,end_year,true);
				parameterLabel = "Top 5 Export (tonnes)";
				objectiveLabel = "Total Export Value: ";
			}
		}
		
		if(result.isEmpty() || result.length() == 0 || result == null) {
			Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText(null);
			windowAlert.setContentText("Your search did not produce any results");
			windowAlert.setTitle("Attention");
			windowAlert.showAndWait();
						
        	return;
		}
		else {
			Stage dialogStage = new Stage();
	        Scene scene;
	        
	        String resource = "ResultFXML.fxml";
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource(resource));
	        
	        Parent root = null;
			try {
				root = (Parent) loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	        ResultController controller = loader.getController();
	        controller.initResult(result, region_selected, search, radio_selected, aggregation_selected, parameterLabel, objectiveLabel, top5, food_selected, start_year, end_year);
	        
			scene = new Scene(root);
	        dialogStage.setTitle("Analysis result");
	        dialogStage.setScene(scene);
	        dialogStage.show();
		}
	}
	
	public void signup() {
		//opening a new window with a new controller
        Stage dialogStage = new Stage();
        Scene scene;
        
        String resource = "RegistrationFXML.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        
        
        Parent root;
		try {
			root = (Parent) loader.load();
			scene = new Scene(root);
	        dialogStage.setTitle("Registration");
	        RegistrationController controller = loader.getController();
	        controller.init(list_food);
	        dialogStage.initModality(Modality.APPLICATION_MODAL);
	        dialogStage.setScene(scene);
	        dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
        		
        
	}
	
	public void login() {
		//opening a new window with a new controller
        Stage dialogStage = new Stage();
        Scene scene;
        
        String resource = "LoginFXML.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        
        Parent root;
		try {
			root = (Parent) loader.load();
			scene = new Scene(root);
	        dialogStage.setTitle("Login");
	        dialogStage.initModality(Modality.APPLICATION_MODAL);
	        dialogStage.setScene(scene);
	        dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
        		
        LoginController controller = loader.getController();
	}
	
	//Utility function used to fill the food combobox
	private void setFoodList() {
		System.out.println("start getFood()");
		list_food = new ArrayList<String>();
		list_food.addAll(MongoHandler.getFood());
		System.out.println("end getFood()");
		food_list = FXCollections.observableList(list_food);
		food_comboBox.setItems(food_list);
	}
	
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
		//filling the aggregation combobox
		aggregation_list = FXCollections.observableArrayList("Sum", "Average", "Top 5");
		aggregation.setItems(aggregation_list);
		
		//filling the continent combobox
		continent_list = FXCollections.observableArrayList("World", 
				"Africa", "North America", "South America", "Asia", "Europe", "Oceania");
		continent_comboBox.setItems(continent_list);
		
		//filling food combobox
		List<String> list = new ArrayList<String>();
		list.add("Wait...");
		food_list = FXCollections.observableList(list); //MongoHandler.getFood());
		food_comboBox.setItems(food_list);
		
		//creating the toggle group
		group = new ToggleGroup();
		rb_production.setToggleGroup(group);
		rb_import.setToggleGroup(group);
		rb_export.setToggleGroup(group);
		
		//thread to filling the food combobox without waiting at startup
		new Thread(() -> {
	        try {
	            Thread.sleep(10);
	            setFoodList();
	        }
	        catch (Exception e){
	            System.err.println(e);
	        }
	    }).start();
	 } 
}
