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
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements Initializable {
	//login fields
	@FXML private TextField username_field;
	@FXML private TextField password_field;
		
		
	public void login(ActionEvent event) throws IOException {
		String username = username_field.getText();
		String password = password_field.getText();
		
		password = HashClass.convertToSha(password);	
		
		User u;
		
		//searching for credentials
		u = MongoHandler.checkUserCredential(username, password);
		
		if(u != null && !u.getUsername().equals("admin")) {
			//closing the window
			Stage oldStage = new Stage();
            Node source = (Node) event.getSource();
                oldStage = (Stage) source.getScene().getWindow();
                oldStage.close();
                
			//opening a new window with a new controller
	        Stage dialogStage = new Stage();
	        Scene scene;
	        
	        String resource = "CompanyFXML.fxml";
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource(resource));
	        Parent root = (Parent) loader.load();
	        
	        CompanyController controller = loader.getController();
	        controller.initCompany(u);
		        
			scene = new Scene(root);
	        dialogStage.setTitle("Company Account");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.setScene(scene);
		    dialogStage.show();
			
		}else if(u!= null && u.getUsername().equals("admin")) {
			//closing the window
			Stage oldStage = new Stage();
            Node source = (Node) event.getSource();
                oldStage = (Stage) source.getScene().getWindow();
                oldStage.close();
                
			//opening a new window with a new controller
	        Stage dialogStage = new Stage();
	        Scene scene;
	        
	        String resource = "AdminFXML.fxml";
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(getClass().getResource(resource));
	        Parent root = (Parent) loader.load();
	        
	        AdminController controller = loader.getController();
		        
			scene = new Scene(root);
	        dialogStage.setTitle("Admin Account");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.setScene(scene);
		    dialogStage.show();
		}else {
			username_field.clear();
			password_field.clear();
			
			Alert windowAlert = new Alert(AlertType.INFORMATION);
			windowAlert.setHeaderText("Wrong username or password");
			windowAlert.setTitle("Warning");
			windowAlert.showAndWait();
		}
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
		username_field.requestFocus();
	}
}