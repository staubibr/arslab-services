package parsers.cdpp;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import components.FilesMap;
import components.Helper;
import models.simulation.MessageCA;
import models.simulation.Port;
import models.simulation.Structure;
import models.simulation.StructureInfo;
import parsers.ILogParser;
import parsers.shared.Ma;
import parsers.shared.Val;

public class CellDevs implements ILogParser {

	private static final String TEMPLATE = "{\"value\":${0}}";

	@Override
	public Structure Parse(FilesMap files) throws IOException {
		Ma maParser = new Ma();
		
		Structure structure = maParser.ParseCA(files.FindStream(".ma"), TEMPLATE);
		
		structure.setInfo(new StructureInfo(files.FindName(".ma"), "CDpp", "Cell-DEVS"));

		FixStructure(structure);
		
		ParseLog(structure, files.FindStream(".val"), files.FindStream(".log"));
				
		return structure;
	}
	
	private static void FixStructure(Structure structure) {
		structure.getNodes().forEach(m -> {
			structure.CreatePort(m, "out", Port.Type.OUTPUT, TEMPLATE);
		});
	}
	
	private void ParseLog(Structure structure, InputStream val, InputStream log) throws IOException {	
		// TODO: Do CDpp models always have a single model?	
		Port port0 = structure.FindPort(structure.getNodes().get(0), "out");
		
		// Merge all possible 00:000 frame messages (val > rows > global)
		List<MessageCA> initial = structure.MergeFrames(structure.GlobalFrame(port0), structure.RowFrame(port0));
		
		Val valParser = new Val();
		
		if (val != null) initial = structure.MergeFrames(initial, valParser.Parse(val, port0));
		
		structure.getTimesteps().add("00:00:00:000");
		
		List<MessageCA> messages = new ArrayList<MessageCA>();
		
		Helper.ReadFile(log, (String l) -> {
			// Mensaje Y / 00:00:05:000 / lug(9,35,0)(1873) / out /    101.00000 para lug(02)
			// probably empty line
			if (!l.startsWith("Mensaje Y")) return;
			
			String[] split = Arrays.stream(l.split("/"))
								   .map(s -> s.trim())
								   .toArray(String[]::new);

			String[] tmp1 = split[2].split("\\(");
			String[] tmp2 = tmp1[1].substring(0, tmp1[1].length() - 1).split(",");
			
			String t = split[1]; 														// time
			// String m = tmp1[0];														// model name;					
			// String p = split[3];														// port
			String v = split[4].split("\\s+")[0];
			
			int[] c = new int[3];

			if (tmp2.length < 2) return; 
			
			c[0] = Integer.parseInt(tmp2[0]);
			c[1] = Integer.parseInt(tmp2[1]);
			c[2] = (tmp2.length == 2) ? 0 : Integer.parseInt(tmp2[2]);
			
			// Magic
			BigDecimal number = new BigDecimal(v);  
			
			v = number.stripTrailingZeros().toPlainString();
			
			if (!structure.getTimesteps().contains(t)) structure.getTimesteps().add(t);

			// Is it always on the same port for Cell-DEVS?
			// Port port = structure.FindPort(m, p);
			
			messages.add(new MessageCA(structure.getTimesteps().size() - 1, port0, c, v));
		});
		
		initial.addAll(messages);
		
		structure.setMessages(initial);
	}
	
	public Boolean Validate(FilesMap files) throws IOException {
		InputStream ma = files.get(files.FindKey(".ma"));
		InputStream log = files.get(files.FindKey(".log"));
		
		if (ma == null || log == null) return false;

		List<String> lines = Helper.ReadNLines(ma, 10);
		
		ma.reset();
		
		long count = lines.stream().filter((String l) -> l.contains("type") && l.contains("cell"))
									.count();
	    
	    return count > 0;
	}
}