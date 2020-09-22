package sim.services.controllers;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import sim.rise.ext.FrameworkService;


@RestController
public class FrameworkController {

	private FrameworkService service; 

	/*
	 *  View Framework
	 */
	@GetMapping(path = "/workspaces/{username}/{servicetype}/{framework}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getFramework(@PathVariable String username,@PathVariable String servicetype,@PathVariable String framework) throws IOException {
		JSONObject frameworkJson = service.getFramework(username,servicetype,framework);
		return ResponseEntity.status(HttpStatus.OK).body(frameworkJson.toString());

	}
	@PostMapping(path = "/workspaces/{username}/{servicetype}/{framework}/{zdirvar}/{oldPassword}")
	public String addFramework(@PathVariable String username, @PathVariable String servicetype,@PathVariable String framework,@PathVariable String  zdirvar, @PathVariable String oldPassword,
			@RequestPart(value = "file", required = true) MultipartFile file,@RequestPart(value = "json", required = true) String configframework) throws IOException {
	
		int x = service.frameworkPOST(username, servicetype,framework, zdirvar, oldPassword, file,configframework);
		if (x == 200)
			return "Status = 200 : Framework Updated";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured while updating Framework";
	}
	@DeleteMapping(path = "/workspaces/{username}/{servicetype}/{framework}/{oldPassword}")
	public String deleteFramework(@PathVariable String username, @PathVariable String servicetype,@PathVariable String framework, @PathVariable String oldPassword) throws IOException {
		int x = service.deleteFramework(username, servicetype,framework, oldPassword);
		if (x == 200)
			return "Status = 200 : Framework Deleted";

		else
			return "Status : " + Integer.toString(x) + "\n Problem occured while deleting Framework";
	}
	
	
}
