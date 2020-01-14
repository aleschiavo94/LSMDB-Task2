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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DocumentController implements Initializable {
	//live search
	@FXML private TextField search_field;
	
	//start and end date
	@FXML private DatePicker start_date;
	@FXML private DatePicker end_date;
		private LocalDate start_period;
		private LocalDate end_period;
	
	//continent combobox
	@FXML private ComboBox<String> continent_comboBox;
		private ObservableList<String> continent_list;
		
	//food combobox
	@FXML private ComboBox<String> food_comboBox;
		private ObservableList<String> food_list;
		
	//radio button 
	@FXML RadioButton rb_production;
	@FXML RadioButton rb_import;
	@FXML RadioButton rb_export;
		private ToggleGroup group;
			
	
	public void submit() {
		//getting the values
		start_period = start_date.getValue();
		end_period = end_date.getValue();
		
		//cleaning the fields
		start_date.getEditor().clear();
		end_date.getEditor().clear();
		
		group.getToggles().clear();
		
		
		//opening a new window with a new controller
        Stage dialogStage = new Stage();
        Scene scene;
        
        String resource = "ResultFXML.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        
        Parent root;
		try {
			root = (Parent) loader.load();
			scene = new Scene(root);
	        dialogStage.setTitle("Analysis result");
	        dialogStage.setScene(scene);
	        dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
        		
        ResultController controller = loader.getController();
              
         
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
		//filling the continent combobox
		continent_list = FXCollections.observableArrayList("World", 
				"Africa", "Americas", "Asia", "Europe", "Oceania");
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
