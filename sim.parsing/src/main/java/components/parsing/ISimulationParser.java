package components.parsing;

import java.io.IOException;
import java.util.HashMap;

import components.simulation.Messages;
import components.simulation.Structure;

public interface ISimulationParser {
	public void SetInfo(Structure structure, HashMap<String, byte[]> files);
	
	public Structure ParseStructure(HashMap<String, byte[]> files) throws IOException;

	public Messages ParseResults(Structure structure, HashMap<String, byte[]> files) throws IOException;

	public Boolean Detect(HashMap<String, byte[]> files) throws IOException;
}