package task2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChangesController implements Initializable{
	private User current_user;
	
	@FXML private TextField username_field;
	@FXML private PasswordField password_field;
	@FXML private TextField companyName_field;
	@FXML private TextField address_field;
	@FXML private TextField country_field;
	@FXML private TextField email_field;
	@FXML private TextField number_field;
	@FXML private Button update_button;
	@FXML private ComboBox<String> business_field;
		private ObservableList<String> food_list;
		
		private String old_username = null;
		
	public void initUser(User u) {
		this.current_user=new User(u);
		this.old_username = this.current_user.getUsername();
	}
	
	public void update() throws IOException {
			String username = username_field.getText();
			String password = password_field.getText();
			String companyName = companyName_field.getText();
			String address = address_field.getText();
			String country = country_field.getText();
			String email = email_field.getText();
			String number = number_field.getText();
			
			
			
			//verifying the format of number field
	        if(!number.matches("\\d*")) {
	        	number_field.clear();
	        	number_field.setPromptText("Insert a number");
	        	number_field.setStyle("-fx-prompt-text-fill: red;");
	        	return;
			}
	        
	        User new_user = new User(current_user);
	        
	        //verifying full fields
	        if(username.length() != 0) {
	        	new_user.setUsername(username);
	        }
	        if(password.length() != 0) {
	        	new_user.setPassword(password);
	        }
	        if(companyName.length() != 0) {
	        	new_user.setCompanyName(companyName);
	        }
	        if(address.length() != 0) {
	        	new_user.setAddress(address);
	        }
	        if(country.length() != 0) {
	        	new_user.setCountry(country);
	        }
	        if(email.length() != 0) {
	        	new_user.setEmail(email);
	        }
	        if(number.length() != 0) {
	        	new_user.setNumber(number);
	        }
	        if(!business_field.getSelectionModel().isEmpty()) {
	        	new_user.setCoreBusiness(business_field.getSelectionModel().getSelectedItem().toString());
	        }
	        	       	
	        MongoHandler.changeInformation(new_user, old_username);
	        
	        //closing the window
	       	Stage stage = (Stage) update_button.getScene().getWindow();
	       	stage.close();
	       	
	       	//opening a new window with a new controller
	        Stage dialogStage = new Stage();
	        Scene scene;
	        
	        String resource = "CompanyFXML.fxml";
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource(resource));
	        Parent root = (Parent) loader.load();
	        
	        CompanyController controller = loader.getController();
	        controller.initCompany(new_user);
		        
			scene = new Scene(root);
	        dialogStage.setTitle("Company Account");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.setScene(scene);
		    dialogStage.show();
	        
		}
	
	private void setFoodList() {
		System.out.println("start getFood()");
		List<String> list_food = new ArrayList<String>();
		list_food.addAll(MongoHandler.getFood());
		System.out.println("end getFood()");
		food_list = FXCollections.observableList(list_food);
		business_field.setItems(food_list);
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
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
