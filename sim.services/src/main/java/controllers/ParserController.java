package controllers;

import java.io.IOException;
import java.util.HashMap;
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
import components.Utilities;
import components.parsing.ISimulationParser;
import components.parsing.IStyleParser;
import components.simulation.Messages;
import components.simulation.Structure;
import components.style.Style;
import components.utilities.Helper;
import parsers.style.PaletteTypeAParser;
import parsers.style.PaletteTypeBParser;
import parsers.utilities.Auto;

@CrossOrigin(origins="*")
@RestController
public class ParserController {
	
	private ResponseEntity<InputStreamResource> ParseStyle(IStyleParser parser, List<MultipartFile> multipartFiles) throws IOException {
		HashMap<String, byte[]> files = Utilities.Convert(multipartFiles);
		
		var result = parser.ParseStyle(files);
				
		return Utilities.JsonFileResponse("style", result);
	}

	private ResponseEntity<byte[]> ParseSimulation(ISimulationParser parser, @RequestParam("files") List<MultipartFile> multipartFiles) throws IOException
	{    	        
		HashMap<String, byte[]> files = Utilities.Convert(multipartFiles);
					
		Structure structure = parser.ParseStructure(files);
		
		Messages messages = parser.ParseResults(structure, files);
		
		return Utilities.ByteArrayResponse(structure.getName(), Helper.MakeZip(structure, messages));
	}
	
	@PostMapping(path="/parser/palette/typeA")
	public ResponseEntity<InputStreamResource> parserPaletteTypeA(@RequestParam("files") List<MultipartFile> files) throws IOException
	{    	    
		return ParseStyle(new PaletteTypeAParser(), files);
	}
	
	@PostMapping(path="/parser/palette/typeB")
	public ResponseEntity<InputStreamResource> parserPaletteTypeB(@RequestParam("files") List<MultipartFile> files) throws IOException
	{    	  
		return ParseStyle(new PaletteTypeBParser(), files);
	}
	
	@PostMapping(path="/parser/cdpp/devs")
	public ResponseEntity<byte[]> parserCdppDevs(@RequestParam("files") List<MultipartFile> files) throws IOException
	{ 
		return this.ParseSimulation(new parsers.cdpp.devs.SimulationParser(), files);
	}

	@PostMapping(path="/parser/cdpp/celldevs")
	public ResponseEntity<byte[]> parserCdppCellDevs(@RequestParam("files") List<MultipartFile> files) throws IOException
	{
		return this.ParseSimulation(new parsers.cdpp.celldevs.SimulationParser(), files);
	}

	@PostMapping(path="/parser/lopez/celldevs")
	public ResponseEntity<byte[]> parserLopezCellDevs(@RequestParam("files") List<MultipartFile> files) throws IOException
	{    	  
		return this.ParseSimulation(new parsers.lopez.celldevs.SimulationParser(), files);
	}

	@PostMapping(path="/parser/cadmium/devs")
	public ResponseEntity<byte[]> parserCadmiumDevs(@RequestParam("files") List<MultipartFile> files) throws IOException
	{    	  
		return this.ParseSimulation(new parsers.cadmium.devs.SimulationParser(), files);
	}

	@PostMapping(path="/parser/cadmium/celldevs")
	public ResponseEntity<byte[]> parserCadmiumCellDevs(@RequestParam("files") List<MultipartFile> files) throws IOException
	{    	  
		return this.ParseSimulation(new parsers.cadmium.celldevs.SimulationParser(), files);
	}

	@PostMapping(path="/parser/cadmium/irregular")
	public ResponseEntity<byte[]> parserCadmiumIrregular(@RequestParam("files") List<MultipartFile> files) throws IOException
	{    	  
		return this.ParseSimulation(new parsers.cadmium.irregular.SimulationParser(), files);
	}

	@PostMapping(path="/parser/auto")
	public ResponseEntity<byte[]> parserAuto(@RequestParam("files") List<MultipartFile> mFiles) throws IOException
	{
		HashMap<String, byte[]> files = Utilities.Convert(mFiles);
		 
		ISimulationParser parser = Auto.DetectSimulationParser(files);

		if (parser == null) throw new CustomException(HttpStatus.BAD_REQUEST, "Unable to automatically detect parser from files.");
		
		Structure structure = parser.ParseStructure(files);
		
		Messages messages = parser.ParseResults(structure, files);
		
		IStyleParser sParser = Auto.DetectStyleParser(files);
		
		Style style = (sParser == null) ? null : sParser.ParseStyle(files);

		return Utilities.ByteArrayResponse(structure.getName(), Helper.MakeZip(structure, messages, style));
	}
    
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<String> handleException(CustomException e) {
	    return ResponseEntity.status(e.getStatus()).body(e.getMessage());
	}
}