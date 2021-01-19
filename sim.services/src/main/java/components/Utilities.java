package components;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utilities {

	public static HashMap<String, byte[]> Convert(List<MultipartFile> files) throws IOException {
		HashMap<String, byte[]> map = new HashMap<String, byte[]>();
		
		// Stupid lambda requires a try catch block in the lambda function
		for (int i = 0; i < files.size(); i++) {
			InputStream ipt = files.get(i).getInputStream();
			
			map.put(files.get(i).getOriginalFilename(), ipt.readAllBytes());
		}
		
		return map;
	}
	
	public static ResponseEntity<byte[]> ByteArrayResponse(String filename, byte[] buffer) throws JsonProcessingException {			
		return ResponseEntity.ok()
	   	        .contentLength(buffer.length)
	   	        .header("Content-Disposition", "attachment; filename=\"" + filename + ".zip\"")
	   	        .header("Access-Control-Allow-Headers", "Content-Disposition")
	   	        .header("Access-Control-Expose-Headers", "Content-Disposition")
	   	        .contentType(MediaType.APPLICATION_OCTET_STREAM)
	   	        .body(buffer);
	}
    
	public static ResponseEntity<InputStreamResource> FileResponse(String filename, byte[] buffer) throws JsonProcessingException {	
	   	return ResponseEntity.ok()
	   	        .contentLength(buffer.length)
	   	        .header("Content-Disposition", "attachment; filename=\"" + filename + ".json\"")
	   	        .header("Access-Control-Allow-Headers", "Content-Disposition")
	   	        .header("Access-Control-Expose-Headers", "Content-Disposition")
	   	        .body(new InputStreamResource(new ByteArrayInputStream(buffer)));
	}
	
	public static ResponseEntity<InputStreamResource> JsonFileResponse(String filename, Object object) throws JsonProcessingException {
	   	ObjectMapper mapper = new ObjectMapper();
	   	
	   	mapper.setSerializationInclusion(Include.NON_EMPTY); 
	   	
	   	byte[] buf = mapper.writeValueAsBytes(object);
	
	   	return Utilities.FileResponse(filename, buf);
	}
}
