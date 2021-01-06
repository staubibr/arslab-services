package parsers.lopez;

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

// TODO: This is very similar to CDpp Cell-DEVS, maybe they could be combined
public class CellDevs implements ILogParser {

	private static final String TEMPLATE = "{\"value\":${0}}";
		
	public Structure Parse(FilesMap files)  throws IOException {
		Ma maParser = new Ma();
		
		Structure structure = maParser.ParseCA(files.FindStream(".ma"), TEMPLATE);
		
		structure.setInfo(new StructureInfo(files.FindName(".ma"), "Lopez", "Cell-DEVS"));
		
		FixStructure(structure);
		
		ParseLog(structure, files.FindStream(".log"));
		
		return structure;
	}
	
	private static void FixStructure(Structure structure) {
		structure.getPorts().forEach(p -> p.setName("out_" + p.getName()));
		
		structure.getNodes().forEach(m -> {
			structure.CreatePort(m, "out", Port.Type.OUTPUT, TEMPLATE);
		});
	}
	
	private static void ParseLog(Structure structure, InputStream log) throws IOException {				
		List<MessageCA> messages = new ArrayList<MessageCA>();
		
		Helper.ReadFile(log, (String l) -> {
			// 0 / L / Y / 00:00:00:000:0 / region(0,0)(02) / out_scenario4 /      0.00000 / region(01)
			// probably empty line
			if (!l.startsWith("0 / L / Y")) return;
			
			String[] split = Arrays.stream(l.split("/"))
								   .map(s -> s.trim())
								   .toArray(String[]::new);

			String[] tmp1 = split[4].split("\\(");
			String[] tmp2 = tmp1[1].substring(0, tmp1[1].length() - 1).split(",");

			String t = split[3].trim();													// time
			String m = tmp1[0];															// model name
			String p = split[5];														// port
			String v = split[6];														// value

			int[] c = new int[3];

			c[0] = Integer.parseInt(tmp2[0]);
			c[1] = Integer.parseInt(tmp2[1]);
			c[2] = (tmp2.length == 2) ? 0 : Integer.parseInt(tmp2[2]);
			
			// Magic
			BigDecimal number = new BigDecimal(v);  
			
			v = number.stripTrailingZeros().toPlainString();
			
			if (!structure.getTimesteps().contains(t)) structure.getTimesteps().add(t);

			Port port = structure.FindPort(m, p);
			
			messages.add(new MessageCA(structure.getTimesteps().size() - 1, port, c, v));
		});
		
		log.close();
		
		structure.setMessages(messages);
	}
	
	public Boolean Validate(FilesMap files) throws IOException {
		String ma = files.FindKey(".ma");
		InputStream log = files.FindStream(".log");

		if (ma == null || log == null) return false;

		List<String> lines = Helper.ReadNLines(log, 1);
		
		log.close();
		
		// 0 / L / Y is the Lopez format, as far as I know, Lopez only does Cell-DEVS
		return (lines.get(0).contains("0 / L / "));
	}
}