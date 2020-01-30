package task2;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import task2.HashClass;


public class RegistrationController implements Initializable {
	@FXML private TextField username_field;
	@FXML private PasswordField password_field;
	@FXML private TextField companyName_field;
	@FXML private TextField address_field;
	@FXML private TextField country_field;
	@FXML private TextField email_field;
	@FXML private TextField number_field;
	@FXML private Button submit_button;
	@FXML private ComboBox<String> business_field;
		private ObservableList<String> food_list;
	
	
	
	public void submit() {
		String username = username_field.getText();
		String password = password_field.getText();
		String companyName = companyName_field.getText();
		String address = address_field.getText();
		String country = country_field.getText();
		String email = email_field.getText();
		String number = number_field.getText();
		String business;
		
		//verifying the format of number field
        if(!number.matches("\\d*")) {
        	number_field.clear();
        	number_field.setPromptText("Insert a number");
        	number_field.setStyle("-fx-prompt-text-fill: red;");
        	return;
		}
        
      //verifying full fields
        if(username.length() == 0 || password.length() == 0 || companyName.length() == 0 || address.length() == 0 || 
        		country.length() == 0 || email.length() == 0 || number.length() == 0 || business_field.getSelectionModel().isEmpty()) {
        	
        	Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText("Please fill all the fields");
			windowAlert.setTitle("Warning");
			windowAlert.showAndWait();
        	return;
            
        }
        
        business= business_field.getSelectionModel().getSelectedItem().toString();
        

        User result;
        
        //checking whether the user is already signed up
        result = MongoHandler.getUserByUsername(username);
        
        if(result != null) {
        	username_field.clear();
        	password_field.clear();
        	
        	username_field.setPromptText("Account già in uso");
        	password_field.setPromptText("Account già in uso");
        	username_field.setStyle("-fx-prompt-text-fill: red;");
        	password_field.setStyle("-fx-prompt-text-fill: red;");
        	return;
        }else {
        	
        	password = HashClass.convertToSha(password);
        	
        	//inserting the new account
        	User u = new User(username, password, companyName, address, country, email, number, business);
        	
        	MongoHandler.insertUser(u);
        	
        	//closing the window
        	Stage stage = (Stage) submit_button.getScene().getWindow();
        	stage.close();
        }
	}
	
	//Utility function used to fill the food combobox
	private void setFoodList(List<String> list_food) {
		//System.out.println("start getFood()");
		//List<String> list_food = new ArrayList<String>();
		//list_food.addAll(MongoHandler.getFood());
		//System.out.println("end getFood()");
		food_list = FXCollections.observableList(list_food);
		business_field.setItems(food_list);
	}

	@Override
	 public void initialize(URL url, ResourceBundle rb) {
		
		/*List<String> list = new ArrayList<String>();
		list.add("Wait...");
		food_list = FXCollections.observableList(list); //MongoHandler.getFood());
		business_field.setItems(food_list);
		
		new Thread(() -> {
	        try {
	            Thread.sleep(10);
	            
	        }
	        catch (Exception e){
	            System.err.println(e);
	        }
	    }).start();*/
	}
	
	public void init(List<String> list_food) {
		setFoodList(list_food);
	}
}