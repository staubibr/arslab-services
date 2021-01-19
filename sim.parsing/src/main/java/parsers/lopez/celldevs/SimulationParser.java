package parsers.lopez.celldevs;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import components.parsing.ISimulationParser;
import components.simulation.Grid;
import components.simulation.MessageCA;
import components.simulation.Messages;
import components.simulation.Model;
import components.simulation.Port;
import components.simulation.Structure;
import components.simulation.StructureInfo;
import components.utilities.Helper;
import parsers.lopez.celldevs.components.ModelCA;
import parsers.utilities.MA;

public class SimulationParser implements ISimulationParser {

	private static ModelCA current;

	@Override
	public void SetInfo(Structure structure, HashMap<String, byte[]> files) {
		String name = Helper.FindName(files, ".ma");
		
		StructureInfo info = new StructureInfo(name, "Lopez", "Cell-DEVS");
		
		structure.setInfo(info);
	}
    
	@Override
	public Structure ParseStructure(HashMap<String, byte[]> files) throws IOException {
    	List<String> ignore = MA.GetDefaultIgnoreList();
    	
		Structure structure = new Structure();

		this.SetInfo(structure, files);
		
		InputStream ma = Helper.FindFileStream(files, ".ma");
		
		Helper.ReadFile(ma, (String line) -> {
			String[] lr = line.trim().toLowerCase().split(":");
			String l = lr[0].trim();
			
			if (l.startsWith("[")) {
				String name = MA.GetModelName(l);
				
				if (MA.IsIgnored(ignore, name)) return;
				
				current = new ModelCA(MA.GetModelName(l), Model.Type.COUPLED, "[\"out\"]", new Grid());

				structure.AddModel(current);
			}
			
			if (current == null || lr.length < 2) return;
			
			String r = lr[1].trim();

			// NeighborPorts: c ty
			if (l.equals("neighborports")) current.setPorts(MA.GetNeighborPorts(current, r));
			
			else if (l.equals("dim")) current.getGrid().setSize(MA.ReadDim(r));

			// height : 20
			else if (l.equals("height")) current.getGrid().setSizeX(Integer.parseInt(r));

			// width : 50
			else if (l.equals("width")) current.getGrid().setSizeY(Integer.parseInt(r));

			// initialvalue : 0
			else if (l.equals("initialvalue")) current.getGrid().setInitialValue(r);

			// localtransition : RegionBehavior
			else if (l.equals("localtransition") || l.equals("zone")) ignore.add(r);
		});
		
		ma.close();
		
		structure.getNodes().forEach(n -> {
			List<Port> ports = ((ModelCA)n).getPorts();
			
			structure.AddPorts(ports);
							
			n.setTemplate(MA.GetTemplate(ports));
		});
		
		return structure;
	}

	@Override
	public Messages ParseResults(Structure structure, HashMap<String, byte[]> files) throws IOException {
		InputStream log = Helper.FindFileStream(files, ".log");
		
		Messages output = new Messages();
		
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
			
			// Check for timestep, add if missing
			if (!output.getTimesteps().contains(t)) output.getTimesteps().add(t);
						
			Port port = structure.FindPort(m, p);
			
			messages.add(new MessageCA(output.getTimesteps().size() - 1, port, c, v));
		});
		
		log.close();

		output.setMessages(GroupMessages(structure, messages));
		
		return output;
	}

	private List<MessageCA> GroupMessages(Structure structure, List<MessageCA> messages) {
		LinkedHashMap<String, List<MessageCA>> groups = new LinkedHashMap<String, List<MessageCA>>();
		List<MessageCA> output = new ArrayList<MessageCA>();
		
		messages.forEach(m -> {
			String id = m.getTimeId();
			
			if (!groups.containsKey(id)) {
				groups.put(id, new ArrayList<MessageCA>());
			}
			
			groups.get(id).add(m);
		});
		
		groups.forEach((k, g) -> {
			Port port = (Port)g.get(0).getEmitter();
			ModelCA node = (ModelCA)port.getNode();
		
			String[] values = new String[node.getPorts().size()];
			
			g.forEach(m -> {
				int idx = node.PortIndex(m.getEmitter());
				
				values[idx] = m.getValue()[0];
			});
			
			output.add(new MessageCA(g.get(0).getTime(), port.getNode(), g.get(0).getCoord(), values));
		});
		
		return output;
	}
	
	@Override
	public Boolean Detect(HashMap<String, byte[]> files) throws IOException {
		String ma = Helper.FindKey(files, ".ma");
		InputStream log = Helper.FindFileStream(files, ".log");

		if (ma == null || log == null) return false;

		List<String> lines = Helper.ReadNLines(log, 1);
		
		log.close();
		
		// 0 / L / Y is the Lopez format, as far as I know, Lopez only does Cell-DEVS
		return (lines.get(0).contains("0 / L / "));
	}
}
