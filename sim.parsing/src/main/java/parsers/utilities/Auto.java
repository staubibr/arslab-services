package parsers.utilities;

import java.io.IOException;
import java.util.HashMap;

import components.parsing.ISimulationParser;
import components.parsing.IStyleParser;

public class Auto {

	public static ISimulationParser TrySimulationParser(ISimulationParser parser, HashMap<String, byte[]> files) throws IOException {		
		return parser.Detect(files) ? parser : null;		
	}

	public static ISimulationParser DetectSimulationParser(HashMap<String, byte[]> files) throws IOException {
		ISimulationParser parser = TrySimulationParser(new parsers.lopez.celldevs.SimulationParser(), files);
		
		if (parser == null) parser = TrySimulationParser(new parsers.cdpp.celldevs.SimulationParser(), files);
		
		if (parser == null) parser = TrySimulationParser(new parsers.cdpp.devs.SimulationParser(), files);
		
		if (parser == null) parser = TrySimulationParser(new parsers.cadmium.irregular.SimulationParser(), files);
		
		if (parser == null) parser = TrySimulationParser(new parsers.cadmium.celldevs.SimulationParser(), files);
		
		if (parser == null) parser = TrySimulationParser(new parsers.cadmium.devs.SimulationParser(), files);
		
		return parser;
	}

	public static IStyleParser TryStyleParser(IStyleParser parser, HashMap<String, byte[]> files) throws IOException {		
		return parser.Detect(files) ? parser : null;		
	}

	public static IStyleParser DetectStyleParser(HashMap<String, byte[]> files) throws IOException {
		IStyleParser parser = TryStyleParser(new parsers.style.PaletteTypeAParser(), files);
		
		if (parser == null) parser = TryStyleParser(new parsers.style.PaletteTypeBParser(), files);
			
		return parser;
	}
}
