package parsers.cdpp;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import components.FilesMap;
import components.Helper;
import models.simulation.Message;
import models.simulation.Model;
import models.simulation.Port;
import models.simulation.Structure;
import models.simulation.StructureInfo;
import parsers.ILogParser;
import parsers.shared.Ma;

public class Devs implements ILogParser {

	private static final String TEMPLATE = "{\"value\":${0}}";
		
	public Structure Parse(FilesMap files)  throws IOException {
		Ma maParser = new Ma();
		
		Structure structure = maParser.Parse(files.FindStream(".ma"), TEMPLATE);
		
		structure.setInfo(new StructureInfo(files.FindName(".ma"), "CDpp", "DEVS"));
		
		ParseLog(structure, files.FindStream("log"));
				
		return structure;
	}
		
	private static void ParseLog(Structure structure, InputStream log) throws IOException {
		List<Message> messages = new ArrayList<Message>();
				
		Helper.ReadFile(log, (String l) -> {
			// Mensaje Y / 00:00:20:000 / top(01) / packetsent /      1.00000 para Root(00)
			if (!l.startsWith("Mensaje Y")) return;
			
			String[] split = Arrays.stream(l.split("/")).map(s -> s.trim()).toArray(String[]::new);
			
			String[] tmp1 = split[2].split("\\("); 
			String[] tmp2 = split[4].trim().split(" ");

			String m = tmp1[0];		// model name
			
			if (structure.FindNode(m).getType() == Model.Type.COUPLED) return;	// Message corresponds to coupled model, we don't want those.
			 			 			 	
			String t = split[1];	// time
			String p = split[3];	// port
			String v = tmp2[0];		// value

			// Magic
			BigDecimal number = new BigDecimal(v);  
			
			v = number.stripTrailingZeros().toPlainString();
						
			if (!structure.getTimesteps().contains(t)) structure.getTimesteps().add(t);
			
			Port port = structure.FindPort(m, p);
			
			messages.add(new Message(structure.getTimesteps().size() - 1, port, v));
		});
		
		structure.setMessages(messages);
	}

	public Boolean Validate(FilesMap files) throws IOException {
		InputStream ma = files.get(files.FindKey(".ma"));
		InputStream log = files.get(files.FindKey(".log"));

		if (ma == null || log == null) return false;

		List<String> lines = Helper.ReadNLines(ma, 10);
		
		ma.reset();
		
		long count = lines.stream().filter((String l) -> l.contains("type") && l.contains("cell"))
									.count();
	    
	    return count == 0;
	}
}