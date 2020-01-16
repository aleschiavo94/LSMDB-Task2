package task2;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {
	private String username;
	private String password;
	private String companyName;
	private String address;
	private String country;
	private String email;
	private String number;
	private String coreBusiness;
	
	
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
	
	public User(User u) {
		this.username = u.username;
		this.password = u.password;
		this.companyName = u.companyName;
		this.address = u.address;
		this.country = u.country;
		this.email = u.email;
		this.number = u.number;
		this.coreBusiness = u.coreBusiness;
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
    
    
    public void setUsername(String username) {
		this.username=username;
	}
	
	public void setPassword(String pwd) {
		this.password=pwd;
	}
    
    public void setCompanyName(String name) {
    	this.companyName=name;
    }
    
    public void setAddress(String add) {
    	this.address=add;
    }
    
    public void setEmail(String e) {
    	this.email=e;
    }
    
    public void setCountry(String c) {
    	this.country=c;
    }
    
    public void setNumber(String num) {
    	this.number=num;
    }
    public void setCoreBusiness(String core) {
    	this.coreBusiness=core;
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
