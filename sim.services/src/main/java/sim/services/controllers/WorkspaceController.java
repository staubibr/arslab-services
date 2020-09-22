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

import sim.rise.ext.WorkspaceService;

@RestController
public class WorkspaceController {

	private WorkspaceService service;

	@GetMapping(path = "/workspaces", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAllWorkspaces() throws IOException {
		JSONObject workspace = service.getAllWorkspaces();
		
		return ResponseEntity.status(HttpStatus.OK).body(workspace.toString());

	}


	@PutMapping(path = "/workspaces/{username}/{oldPassword}")
	public String updateWorkspace(@PathVariable String username, @PathVariable String oldPassword,
			@RequestBody String accountProperties) throws IOException {
		int x = service.updateWorkspace(username, oldPassword, accountProperties);
		if (x == 200)
			return "Status = 200 : Workspace Updated";

		else if (x == 201)
			return "Status = 201 : Workspace Created";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured while updating Workspace";
	}

	@DeleteMapping(path = "/workspaces/{username}/{oldPassword}")
	public String deleteWorkspace(@PathVariable String username, @PathVariable String oldPassword) throws IOException {
		int x = service.deleteWorkspace(username, oldPassword);
		if (x == 200)
			return "Status = 200 : Workspace Deleted";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured while deleting Workspace";
	}
	
	
	/*
	 * User Workspace
	 */
	
	@GetMapping(path = "/workspaces/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getWorkspace(@PathVariable String username) throws IOException {
		JSONObject workspace = service.getWorkspace(username);
		
		return ResponseEntity.status(HttpStatus.OK).body(workspace.toString());
	}
	
}