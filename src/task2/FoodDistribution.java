package task2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FoodDistribution extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
    	MongoHandler.startMongo();
    	
        Parent root = FXMLLoader.load(getClass().getResource("DocumentFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("World food distribution");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public static void main(String[] args) {
    	launch(args);
    	
    	MongoHandler.closeMongo();
    }
}
