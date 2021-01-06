package parsers.auto;

import java.io.IOException;

import components.FilesMap;
import models.simulation.Structure;
import parsers.ILogParser;
import parsers.IParser;

public class Auto implements IParser {
		
	public Structure Parse(FilesMap files) throws IOException {
		ILogParser parser = DetectParser(files);
		
		if (parser == null) throw new RuntimeException("Unable to automatically detect parser from files.");
		
		Structure result = parser.Parse(files);
		
		return result;
	}
	
	private ILogParser DetectParser(FilesMap files) throws IOException {
		ILogParser parser = TryParser(new parsers.lopez.CellDevs(), files);
		
		if (parser == null) parser = TryParser(new parsers.cdpp.CellDevs(), files);
		
		if (parser == null) parser = TryParser(new parsers.cdpp.Devs(), files);
		
		if (parser == null) parser = TryParser(new parsers.cadmium.Irregular(), files);
		
		if (parser == null) parser = TryParser(new parsers.cadmium.CellDevs(), files);
		
		if (parser == null) parser = TryParser(new parsers.cadmium.Devs(), files);
				
		return parser;
	}
	
	private ILogParser TryParser(ILogParser parser, FilesMap files) throws IOException {
		Boolean valid = parser.Validate(files);
		
		return valid ? parser : null;		
	}
}
