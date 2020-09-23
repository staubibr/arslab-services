package parsers.cdpp;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import components.FilesMap;
import components.Helper;
import models.MessageCA;
import models.ModelCA;
import models.Parsed;
import models.Port;
import models.Structure;
import models.StructureInfo;
import parsers.IParser;
import parsers.shared.Ma;
import parsers.shared.Val;

public class CellDevs implements IParser {

	private static final String TEMPLATE = "{\"value\":${0}}";

	@Override
	public Parsed Parse(FilesMap files) throws IOException {
		String name = files.FindName(".ma");
		Structure structure = (new Ma()).ParseCA(files.FindStream(".ma"), TEMPLATE);

		structure.setInfo(new StructureInfo(name, "CDpp", "Cell-DEVS"));

		FixStructure(structure);
		
		List<MessageCA> messages = ParseLog(structure, files.FindStream(".val"), files.FindStream(".log"));
				
		return new Parsed(name, structure, messages);
	}
	
	private static void FixStructure(Structure structure) {
		structure.getNodes().forEach(m -> {
			structure.getPorts().add(new Port(m.name, "out", "output", TEMPLATE));
		});
	}
	
	private List<MessageCA> ParseLog(Structure structure, InputStream val, InputStream log) throws IOException {	
		// TODO: Do CDpp models always have a single model?	
		ModelCA main = (ModelCA)structure.getNodes().get(0);
		
		// Merge all possible 00:000 frame messages (val > rows > global)
		List<MessageCA> initial = Helper.MergeFrames(main.GlobalFrame(), main.RowFrame());
		
		if (val != null) initial = Helper.MergeFrames(initial, (new Val()).Parse(val, main));
		
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
			String m = tmp1[0];															// model name;					
			String p = split[3];														// port
			String v = split[4].split("\\s+")[0];
			
			int[] c = new int[3];

			c[0] = Integer.parseInt(tmp2[0]);
			c[1] = Integer.parseInt(tmp2[1]);
			c[2] = (tmp2.length == 2) ? 0 : Integer.parseInt(tmp2[2]);
			
			// Magic
			BigDecimal number = new BigDecimal(v);  
			
			v = number.stripTrailingZeros().toPlainString();
						
			messages.add(new MessageCA(t, m, c, p, v));
		});
		
		initial.addAll(messages);
		
		return initial;
	}
	
	public static Boolean Validate(FilesMap files) throws IOException {
		String ma = files.FindKey(".ma");
		InputStream log = files.get(files.FindKey(".log"));

		if (ma == null || log == null) return false;

		List<String> lines = Helper.ReadNLines(log, 3);

		if (!lines.get(0).contains("Mensaje")) return false;

		long n1 = lines.get(2).chars().filter(c -> c == '(').count();
		long n2 = lines.get(2).chars().filter(c -> c == ')').count();
		
		// A cell devs message will have one more set of parentheses than a DEVS message because of the coordinates
		// TODO: This is very shifty but simple, and works.
		return n1 == 3 && n2 == 3;
	}
}