package task2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AdminController implements Initializable{
	@FXML private Button logout_button;
	@FXML private Button delete_button;
	@FXML private TextField username_field;
	@FXML private TextArea user_area;
	@FXML private Label user_information;
	@FXML private ListView<String> user_list;
		private ObservableList<String> observable_user_list;
		private String selected_user;
	
	public void logout(ActionEvent event) throws IOException {
		//closing the window
    	Stage stage = (Stage) logout_button.getScene().getWindow();
    	stage.close();
          
	}
	
	public void fileChooser() {
		Stage fileChooserStage = new Stage();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
		    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
		);
		
		File selectedFile = fileChooser.showOpenDialog(fileChooserStage);
		if(selectedFile!= null) {
			//inserimento in mongodb
			System.out.println(selectedFile.getName());
		}
		
	}
	
	//showing user's informations
	public void showInformations(MouseEvent event) throws IOException{
		selected_user = user_list.getSelectionModel().getSelectedItem();
		
		User u=MongoHandler.getUserByUsername(selected_user);
		
		if (event.getClickCount() == 2 && selected_user != null){
			String newLine = "\n";
			
			user_information.setVisible(true);
			user_area.setVisible(true);
			user_area.setText("Username:   "+u.getUsername()+newLine+
					"Company Name:   "+u.getCompanyName()+newLine+
					"Address:   "+u.getAddress()+newLine+
					"Country:   "+u.getCountry()+newLine+
					"Email:   "+u.getEmail()+newLine+
					"Telephone number:   "+u.getNumber()+newLine+
					"Core business:   "+u.getCoreBusiness());
		}
	}
	
	public void deleteAccount() {
		Alert windowAlert = new Alert(AlertType.CONFIRMATION);
		windowAlert.setHeaderText(null);
		windowAlert.setContentText("Are you sure you want to cancel the account?");
		windowAlert.setTitle("Are you sure?");
    	
		Optional<ButtonType> result = windowAlert.showAndWait();
		if (result.get() == ButtonType.OK){
			String username = username_field.getText();
		    MongoHandler.deleteAccountByUsername(username);
			
			observable_user_list.clear();
			observable_user_list.addAll(MongoHandler.getAllUsers());
			user_list.setItems(observable_user_list);
			
			username_field.clear();
		} else {
		    return;
		}
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
			observable_user_list=FXCollections.observableArrayList(MongoHandler.getAllUsers());
			user_list.setItems(observable_user_list);
			
			user_area.setVisible(false);
			user_information.setVisible(false);
	}
}
