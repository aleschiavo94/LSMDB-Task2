package task2;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
	//live search
	@FXML private TextField search_field;
	
	//start and end date
//	@FXML private DatePicker start_date;
//	@FXML private DatePicker end_date;
//		private LocalDate start_period;
//		private LocalDate end_period;
	
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
		
			
	public void submit() throws IOException{
		//getting the values
		if(food_comboBox.getSelectionModel().isEmpty() || 
			(continent_comboBox.getSelectionModel().isEmpty() && search_field.getLength() == 0) ||
			aggregation.getSelectionModel().isEmpty() ||
			!group.getSelectedToggle().isSelected() || 
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
		else if((((Labeled) group.getSelectedToggle()).getText().equals("Import") || ((Labeled) group.getSelectedToggle()).getText().equals("Export"))
				&& aggregation.getSelectionModel().getSelectedItem().toString().equals("Top 5")) {
			Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText(null);
			windowAlert.setContentText("Top 5 aggregation is possible only with Production");
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
			food_comboBox.getEditor().clear();
			continent_comboBox.getEditor().clear();
			start_date.clear();
			end_date.clear();
			search_field.clear();
			aggregation.getEditor().clear();
			
			group.getToggles().clear();
			
			
			//opening a new window with a new controller
			if(!aggregation.getSelectionModel().getSelectedItem().toString().equals("Top 5")) {
		        Stage dialogStage = new Stage();
		        Scene scene;
		        
		        String resource = "ResultFXML.fxml";
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource(resource));
		        
		        Parent root = (Parent) loader.load();
		        
		        ResultController controller = loader.getController();
		        controller.initResult(food_selected, region_selected, search, radio_selected, start_year, end_year, aggregation_selected);
		        
				scene = new Scene(root);
		        dialogStage.setTitle("Analysis result");
		        dialogStage.setScene(scene);
		        dialogStage.show();
			}else {
				System.out.println("TOP 5 NON IMPLEMENTATA ");
			}
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
	        dialogStage.initModality(Modality.APPLICATION_MODAL);
	        dialogStage.setScene(scene);
	        dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
        		
        RegistrationController controller = loader.getController();
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
		List<String> list_food = new ArrayList<String>();
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
