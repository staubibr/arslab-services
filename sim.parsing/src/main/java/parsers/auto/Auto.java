package parsers.auto;

import java.io.BufferedInputStream;
import java.io.IOException;

import components.FilesMap;
import models.Parsed;
import parsers.IParser;
import parsers.shared.Palette;

public class Auto implements IParser {
		
	public Parsed Parse(FilesMap files) throws IOException {
		// Parse structure and messages first. Inputstream will have to be reset because
		// the automatic detection process reads a couple of lines
		files.Mark(0);

		IParser parser = DetectParser(files);
		
		if (parser == null) throw new RuntimeException("Unable to automatically detect parser from files.");

		files.Reset();
		
		Parsed result = parser.Parse(files);
		
		// Parse palette if provided
		BufferedInputStream pal = files.FindStream(".pal");
					
		if (pal != null) result.setPalette((new Palette()).Parse(pal));
		
		return result;
	}
	
	private IParser DetectParser(FilesMap files) throws IOException {		
		if (parsers.lopez.CellDevs.Validate(files)) return new parsers.lopez.CellDevs();

		// TODO: All these resets, got to figure out something better
		files.Reset();
		
		if (parsers.cdpp.CellDevs.Validate(files)) return new parsers.cdpp.CellDevs();

		files.Reset();
		
		if (parsers.cdpp.Devs.Validate(files)) return new parsers.cdpp.Devs();

		files.Reset();
		
		if (parsers.cadmium.CellDevs.Validate(files)) return new parsers.cadmium.CellDevs();

		files.Reset();
		
		if (parsers.cadmium.Devs.Validate(files)) return new parsers.cadmium.Devs();
				
		return null;
	}
}
