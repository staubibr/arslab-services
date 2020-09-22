package sim.services.controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import sim.rise.ext.ResultsService;



@RestController
public class ResultsController {

	private ResultsService service;
	

	/*
	 * Simulation Results -  Get & Delete
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}/{framework}/results")
	public String getSimResults(@PathVariable String username,@PathVariable String servicetype,@PathVariable String framework) throws IOException {
		String x = service.getSimResultsURL(username,servicetype,framework);
		return x;

	}
	@DeleteMapping(path = "/workspaces/{username}/{servicetype}/{framework}/{oldPassword}/results")
	public String deleteSimResults(@PathVariable String username, @PathVariable String servicetype,@PathVariable String framework, 
			@PathVariable String oldPassword) throws IOException {
		int x = service.deleteSimResults(username, servicetype,framework, oldPassword);
		if (x == 200)
			return "Status = 200 : Simulation Results Deleted";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured";
	}
	/*
	 * Simulation Debug - Get & Delete 
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}/{framework}/debug")
	public String getDebugResults(@PathVariable String username,@PathVariable String servicetype,@PathVariable String framework) throws IOException {
		String x = service.getDebugResultsURL(username,servicetype,framework);
		return x;

	}
	@DeleteMapping(path = "/workspaces/{username}/{servicetype}/{framework}/{oldPassword}/debug")
	public String deleteDebugResults(@PathVariable String username, @PathVariable String servicetype,@PathVariable String framework, 
			@PathVariable String oldPassword) throws IOException {
		int x = service.deleteDebugResults(username, servicetype,framework, oldPassword);
		if (x == 200)
			return "Status = 200 : Simulation Debug Results Deleted";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured";
	}
	/*
	 * Simulation Everything - Get  
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}/{framework}/getAllInfo")
	public String getAllInfo(@PathVariable String username,@PathVariable String servicetype,@PathVariable String framework) throws IOException {
		String x = service.getAllInfo(username,servicetype,framework);
		return x;

	}
}
