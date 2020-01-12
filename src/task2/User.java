package task2;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {
	String username;
	String password;
	String companyName;
	String address;
	String country;
	String email;
	String number;
	String coreBusiness;
	
	public User(String username, String password, String companyName, String address, String country, String email, String number, String coreBusiness) {
		this.username = username;
		this.password = password;
		this.companyName = companyName;
		this.address = address;
		this.country = country;
		this.email = email;
		this.number = number;
		this.coreBusiness = coreBusiness;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
    
    public String getCompanyName() {
    	return this.companyName;
    }
    
    public String getAddress() {
    	return this.address;
    }
    
    public String getEmail() {
    	return this.email;
    }
    
    public String getCountry() {
    	return this.country;
    }
    
    public String getNumber() {
    	return this.number;
    }
    public String getCoreBusiness() {
    	return this.coreBusiness;
    }
    
    public void setPassword(String psw){
        this.password = psw;
    }
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		
		json.put("username", this.username);
		json.put("password", this.password);
		json.put("company_name", this.companyName);
		json.put("address", this.address);
		json.put("email", this.email);
		json.put("country", this.country);
		json.put("number", this.number);
		json.put("core_business", this.coreBusiness);
		return json;
	}
}
