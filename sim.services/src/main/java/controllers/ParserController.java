package controllers;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import components.CustomException;
import components.FilesMap;
import components.Helper;
import components.Utilities;
import models.simulation.Structure;
import models.style.Style;
import parsers.ILogParser;
import parsers.IParser;
import parsers.shared.Palette;
 
@RestController
@CrossOrigin(origins = "*")
public class ParserController {
    
	@PostMapping("/parser/palette/typeA")
	public ResponseEntity<InputStreamResource> parserPaletteTypeA(@RequestParam("pal") MultipartFile pal)
	{    	     
		try {   
			Style style = (new Palette()).ParseTypeA(pal.getInputStream());
					
			return Utilities.JsonFileResponse("style", style.getLayers());
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/parser/palette/typeB")
	public ResponseEntity<InputStreamResource> parserPaletteTypeB(@RequestParam("pal") MultipartFile pal)
	{    
		try {	        
			Style style = (new Palette()).ParseTypeB(pal.getInputStream());
			
			return Utilities.JsonFileResponse("style", style.getLayers());
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/parser/palette/auto")
	public ResponseEntity<InputStreamResource> parserPaletteAuto(@RequestParam("files") List<MultipartFile> files)
	{    	 
		try {       
			FilesMap map = Utilities.Convert(files);
	
			map.Mark(0);
			
			Style style = (new Palette()).Parse(map);
			
			return Utilities.JsonFileResponse("style", style.getLayers());
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	  	
	@PostMapping("/parser/auto")
	public ResponseEntity<byte[]> parserAuto(@RequestParam("files") List<MultipartFile> files)
	{
		try {
			FilesMap map = Utilities.Convert(files);
			
			map.Mark(0);
			
			IParser parser = new parsers.auto.Auto();
			Structure result = parser.Parse(map);
			
			// Parse palette if provided
			Palette palParser = new parsers.shared.Palette();
			
			Style style = palParser.Parse(map);
			
			map.Close();
			
			return Utilities.ByteArrayResponse(result.getName(), Helper.MakeZip(result, style));
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@PostMapping("/parser/cdpp/celldevs")
	public ResponseEntity<byte[]> parserCdppCellDevs(@RequestParam("files") List<MultipartFile> files)
	{    	        
		try {
			FilesMap map = Utilities.Convert(files);
			ILogParser parser = new parsers.cdpp.CellDevs();
			Structure result = parser.Parse(map);
			
			map.Close();
		  			  	
			return Utilities.ByteArrayResponse(result.getName(), Helper.MakeZip(result));
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	   
	@PostMapping("/parser/cdpp/devs")
	public ResponseEntity<byte[]> parserCdppDevs(@RequestParam("files") List<MultipartFile> files)
	{    	        
		try {
			FilesMap map = Utilities.Convert(files);
			ILogParser parser = new parsers.cdpp.Devs();
			Structure result = parser.Parse(map);
				
			map.Close();
		  			  	
			return Utilities.ByteArrayResponse(result.getName(), Helper.MakeZip(result));
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	   
	@PostMapping("/parser/lopez/celldevs")
	public ResponseEntity<byte[]> parserLopezCellDevs(@RequestParam("files") List<MultipartFile> files)
	{    	        
		try {
			FilesMap map = Utilities.Convert(files);
			ILogParser parser = new parsers.lopez.CellDevs();
			Structure result = parser.Parse(map);
		  				
			map.Close();
			
			return Utilities.ByteArrayResponse(result.getName(), Helper.MakeZip(result));
		} 
		catch (Exception e) {
		  	throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
    
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<String> handleException(CustomException e) {
	    return ResponseEntity.status(e.getStatus()).body(e.getMessage());
	}
}