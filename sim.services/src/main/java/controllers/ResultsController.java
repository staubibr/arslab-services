package controllers;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import components.CustomException;
import services.ResultsService;

@RestController
public class ResultsController {

	/*
	 * Simulation Results - Get & Delete
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}/{framework}/results")
	public String getSimResults(@PathVariable String username, @PathVariable String servicetype,
			@PathVariable String framework) throws IOException {
		ResultsService service = new ResultsService();
		String x = service.getSimResultsURL(username, servicetype, framework);
		return x;

	}

	@DeleteMapping(path = "/workspaces/{username}/{servicetype}/{framework}/{oldPassword}/results")
	public String deleteSimResults(@PathVariable String username, @PathVariable String servicetype,
			@PathVariable String framework, @PathVariable String oldPassword) throws IOException {
		ResultsService service = new ResultsService();
		int x = service.deleteSimResults(username, servicetype, framework, oldPassword);
		if (x == 200)
			return "Status = 200 : Simulation Results Deleted";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured";
	}

	/*
	 * Simulation Debug - Get & Delete
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}/{framework}/debug")
	public String getDebugResults(@PathVariable String username, @PathVariable String servicetype,
			@PathVariable String framework) throws IOException {
		ResultsService service = new ResultsService();
		String x = service.getDebugResultsURL(username, servicetype, framework);
		return x;

	}

	@DeleteMapping(path = "/workspaces/{username}/{servicetype}/{framework}/{oldPassword}/debug")
	public String deleteDebugResults(@PathVariable String username, @PathVariable String servicetype,
			@PathVariable String framework, @PathVariable String oldPassword) throws IOException {
		ResultsService service = new ResultsService();
		int x = service.deleteDebugResults(username, servicetype, framework, oldPassword);
		if (x == 200)
			return "Status = 200 : Simulation Debug Results Deleted";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured";
	}

	/*
	 * Simulation Parsed Results - Get
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}/{framework}/getParsedResults")

	public ResponseEntity<byte[]> getParsedResults(@PathVariable String username, @PathVariable String servicetype,
			@PathVariable String framework) throws IOException, SQLException {
		try {
			ResultsService service = new ResultsService();
			ResponseEntity<byte[]> x = service.getParsedResults(username, servicetype, framework);
			return x;	
		} catch (Exception e) {
			throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());

		}
		
	}
}
