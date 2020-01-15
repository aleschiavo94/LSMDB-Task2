package task2;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import task2.DocumentController;

public class CompanyController implements Initializable {
	private User current_company;
	
	@FXML private Label username_field;
	@FXML private Label company_field;
	@FXML private Label address_field;
	@FXML private Label country_field;
	@FXML private Label email_field;
	@FXML private Label number_field;
	@FXML private Label business_field;
	@FXML private Button logout_button;
	@FXML private Button modify_button;
	
	public void initCompany(User u) {
		this.current_company = new User(u);
		showCompanyInformation(this.current_company);
	}
	
	public void showCompanyInformation(User u) {
		username_field.setText(current_company.getUsername());
		company_field.setText(current_company.getCompanyName());
		address_field.setText(current_company.getAddress());
		country_field.setText(current_company.getCountry());
		email_field.setText(current_company.getEmail());
		number_field.setText(current_company.getNumber());
		business_field.setText(current_company.getCoreBusiness());
	}
	
	public void logout(ActionEvent event) throws IOException {
		//closing the window
    	Stage stage = (Stage) logout_button.getScene().getWindow();
    	stage.close();
          
	}
	
	public void modify() {
		//closing the window
    	Stage stage = (Stage) modify_button.getScene().getWindow();
    	stage.close();
    	
		//opening a new window with a new controller
        Stage dialogStage = new Stage();
        Scene scene;
        
        String resource = "ChangesFXML.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        
        Parent root;
		try {
			root = (Parent) loader.load();
			scene = new Scene(root);
	        dialogStage.setTitle("Update your information");
	        dialogStage.initModality(Modality.APPLICATION_MODAL);
	        dialogStage.setScene(scene);
	        dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
        		
       ChangesController controller = loader.getController();
       controller.initUser(current_company);
	}
	

	@Override
	 public void initialize(URL url, ResourceBundle rb) {
		
	}
}
