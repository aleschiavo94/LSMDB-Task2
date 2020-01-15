package task2;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class ResultController implements Initializable {

	public void initResult(String food, String region, String country, String aim, String start, String end) {
		System.out.println(food);
		System.out.println(region);
		System.out.println(country);
		System.out.println(aim);
		System.out.println(start);
		System.out.println(end);
		
		MongoHandler.getQueryResult(food,region,country,aim,start,end);
	}
	
	@Override
	 public void initialize(URL url, ResourceBundle rb) {
	
	}
}
