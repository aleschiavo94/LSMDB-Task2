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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import task2.DocumentController;

public class CompanyController implements Initializable {
	private User current_company;
	
	@FXML private Label username_field;
	@FXML private Label password_field;
	@FXML private Label company_field;
	@FXML private Label address_field;
	@FXML private Label country_field;
	@FXML private Label email_field;
	@FXML private Label number_field;
	@FXML private Label business_field;
	
	public void initCompany(User u) {
		this.current_company = new User(u);
		showCompanyInformation(this.current_company);
	}
	
	public void logout(ActionEvent event) throws IOException {
		Stage dialogStage = new Stage();
        Scene scene;
        Node source = (Node) event.getSource();
		 dialogStage = (Stage) source.getScene().getWindow();
         dialogStage.close();
         String resource;
         Parent root;
         
        resource = "DocumentFXML.fxml";
     	FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
       
        root = (Parent) loader.load();
        		
        DocumentController controller = loader.getController();
        
        scene = new Scene(root);
        dialogStage.setTitle("World food distribution");
        dialogStage.setScene(scene);
        dialogStage.show(); 
	}
	
	public void showCompanyInformation(User u) {
		username_field.setText(current_company.getUsername());
		password_field.setText(current_company.getPassword());
		company_field.setText(current_company.getCompanyName());
		address_field.setText(current_company.getAddress());
		country_field.setText(current_company.getCountry());
		email_field.setText(current_company.getEmail());
		number_field.setText(current_company.getNumber());
		business_field.setText(current_company.getCoreBusiness());
	}

	@Override
	 public void initialize(URL url, ResourceBundle rb) {
		
	}
}
