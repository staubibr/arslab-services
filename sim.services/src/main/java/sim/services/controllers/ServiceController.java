package sim.services.controllers;

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

import sim.rise.ext.ServiceTypeDaoService;

@RestController

public class ServiceController {

	private ServiceTypeDaoService service;

	/*
	 * User Workspace Service /View all frameworks in a service
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getWorkspaceService(@PathVariable String username, @PathVariable String servicetype)
			throws IOException {
		JSONObject servicet = service.getWorkspaceService(username, servicetype);
		return ResponseEntity.status(HttpStatus.OK).body(servicet.toString());

	}

	@PutMapping(path = "/workspaces/{username}/{servicetype}/{oldPassword}")
	public String updateService(@PathVariable String username, @PathVariable String servicetype,
			@PathVariable String oldPassword, @RequestBody String accountProperties) throws IOException {
		int x = service.updateService(username, servicetype, oldPassword, accountProperties);
		if (x == 200)
			return "Status = 200 : Service Updated";

		else if (x == 201)
			return "Status = 201 : Service Created";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured while updating Workspace";
	}

	@DeleteMapping(path = "/workspaces/{username}/{servicetype}/{oldPassword}")
	public String deleteService(@PathVariable String username, @PathVariable String servicetype,
			@PathVariable String oldPassword) throws IOException {
		int x = service.deleteService(username, servicetype, oldPassword);
		if (x == 200)
			return "Status = 200 : Service Deleted";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured while deleting Workspace";
	}

}
