package controllers;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import services.AccountsService;

@RestController
public class AccountsController {

	private AccountsService service; 

	@GetMapping(path="/Accounts", produces = MediaType.APPLICATION_JSON_VALUE)  
	public ResponseEntity<String> getAllAccounts() throws IOException  
	{
		AccountsService srv = new AccountsService();
		
		JSONObject accounts = srv.getAccounts();
		
		return ResponseEntity.status(HttpStatus.OK).body(accounts.toString());
	} 
	
	@PutMapping(path="/Accounts/{username}")  
	public String createAccount(@PathVariable String username,@RequestBody String accountProperties) throws IOException  
	{
		int x =	service.createAccount(username,accountProperties);
		
		if (x==201) return "Status = 201 : Account Created";  
		
		else return "Status : " + Integer.toString(x) + "\n Problem occured while creating account";
	}
	
	@GetMapping(path="/DB/Models/{modelName}") 
	public String getModel(@PathVariable String modelName, @RequestBody String data) throws IOException  
	{
		JSONObject json = new JSONObject(data);
		
		String color = json.getString("color");
		int qty = json.getInt("quantity");
		
		return modelName + " has " + Integer.toString(qty) + " " + color + "potatoes.";
	}
	
	@PutMapping(path="/Accounts/{username}/{oldPassword}")  
	public String updateAccount(@PathVariable String username,@PathVariable String oldPassword,@RequestBody String accountProperties) throws IOException  
	{
		int x =	service.updateAccount(username,oldPassword,accountProperties);
		
		if (x==200) return "Status = 200 : Account Updated";  
	
		else return "Status : " + Integer.toString(x) + "\n Problem occured while updating account";
	}
	
	@DeleteMapping(path="/Accounts/{username}")  
	public String deleteAccount(@PathVariable String username) throws IOException  
	{
		int x =	service.deleteAccount(username);
		
		if (x==200) return "Status = 200 : Account Deleted";  
		
		else return "Status : " + Integer.toString(x) + "\n Problem occured while deleting account";
	}
}  