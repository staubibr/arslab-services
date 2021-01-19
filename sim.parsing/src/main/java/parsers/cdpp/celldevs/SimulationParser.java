package parsers.cdpp.celldevs;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import components.parsing.ISimulationParser;
import components.simulation.MessageCA;
import components.simulation.Messages;
import components.simulation.Model;
import components.simulation.ModelCA;
import components.simulation.Port;
import components.simulation.Structure;
import components.simulation.StructureInfo;
import components.utilities.Helper;
import parsers.cdpp.celldevs.components.Grid;
import parsers.utilities.MA;

public class SimulationParser implements ISimulationParser{
	
	private static ModelCA current;

	@Override
	public void SetInfo(Structure structure, HashMap<String, byte[]> files) {
		String name = Helper.FindName(files, ".ma");
		
		StructureInfo info = new StructureInfo(name, "CDpp", "Cell-DEVS");
		
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
				Port port = new Port(current, "out", Port.Type.OUTPUT, null);
				
				structure.AddModel(current);
				structure.AddPort(port);
			}
			
			if (current == null || lr.length < 2) return;
			
			String r = lr[1].trim();
			
			// dim : (30, 30)			
			if (l.equals("dim")) current.getGrid().setSize(MA.ReadDim(r));

			// height : 20
			else if (l.equals("height")) current.getGrid().setSizeX(Integer.parseInt(r));

			// width : 50
			else if (l.equals("width")) current.getGrid().setSizeY(Integer.parseInt(r));

			// initialvalue : 0
			else if (l.equals("initialvalue")) ((Grid)current.getGrid()).setInitialValue(r);

			// initialrowvalue :  1      00111011100011100200
			else if (l.equals("initialrowvalue")) ((Grid)current.getGrid()).getInitialRowValues().add(MA.GetInitialRowValues(r));

			// localtransition : RegionBehavior
			else if (l.equals("localtransition") || l.equals("zone")) ignore.add(r);
		});
		
		ma.close();
	
		return structure;
	}
	
	private List<MessageCA> ParseVal(Structure structure, HashMap<String, byte[]> files) throws IOException {
		InputStream val = Helper.FindFileStream(files, ".val");
		
		if (val == null) return null;
		
		// Port port0 = structure.FindPort(structure.getNodes().get(0), "out");
		ModelCA model0 = (ModelCA)structure.getNodes().get(0);
		
		ArrayList<MessageCA> messages = new ArrayList<MessageCA>();

		// (0,0,0)=100
		Helper.ReadFile(val, (String l) -> {
			// probably empty line
			if (l == null || l.length() < 4) return;

			if (!l.startsWith("(")) throw new RuntimeException("File format does not correspond to a val file.");
			
			l = l.replace(" ",  "");
			
			int cI = l.indexOf('('); // coordinate start
			int cJ = l.indexOf(')'); // coordinate end
			int vI = l.indexOf('='); // value start

			String[] c = l.substring(cI + 1, cJ).replaceAll("\\s+", "").split(",");
			
			int[] coord = new int[3]; 

			coord[0] = Integer.parseInt(c[0]);
			coord[1] = Integer.parseInt(c[1]);
			coord[2] = (c.length == 2) ? 0 : Integer.parseInt(c[2]);
			
			String[] values = l.substring(vI + 1).split(" ");
			
			// TODO: This is a mess, particularly when DEVS model are mixed with Cell-DEVS models
			messages.add(new MessageCA(0, model0, coord, values[0]));
		});
		
		val.close();
		
		return messages;
	}
	
	@Override
	public Messages ParseResults(Structure structure, HashMap<String, byte[]> files) throws IOException {
		InputStream log = Helper.FindFileStream(files, ".log");
		
		// TODO: Do CDpp models always have a single model?
		// Port port0 = structure.FindPort(structure.getNodes().get(0), "out");
		ModelCA model0 = (ModelCA)structure.getNodes().get(0);
		Grid grid = (Grid) model0.getGrid();
		
		// Merge all possible 00:000 frame messages (val > rows > global)
		List<MessageCA> initial = Helper.MergeFrames(grid.GlobalFrame(model0), grid.RowFrame(model0));
		List<MessageCA> val = this.ParseVal(structure, files);
		
		if (val != null) initial = Helper.MergeFrames(initial, val);
		
		Messages output = new Messages();
		
		output.getTimesteps().add("00:00:00:000");
		
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
			
			String t = split[1]; 														// time												// port
			String v = split[4].split("\\s+")[0];
			
			int[] c = new int[3];

			if (tmp2.length < 2) return; 
			
			c[0] = Integer.parseInt(tmp2[0]);
			c[1] = Integer.parseInt(tmp2[1]);
			c[2] = (tmp2.length == 2) ? 0 : Integer.parseInt(tmp2[2]);
			
			// Magic
			BigDecimal number = new BigDecimal(v);  
			
			v = number.stripTrailingZeros().toPlainString();
			
			if (!output.getTimesteps().contains(t)) output.getTimesteps().add(t);

			messages.add(new MessageCA(output.getTimesteps().size() - 1, model0, c, v));
		});
		
		log.close();
		
		initial.addAll(messages);
		
		output.setMessages(initial);
		
		return output;
	}

	@Override
	public Boolean Detect(HashMap<String, byte[]> files) throws IOException {
		InputStream ma = Helper.FindFileStream(files, ".ma");
		InputStream log = Helper.FindFileStream(files, ".log");
		
		if (ma == null || log == null) return false;

		List<String> lines = Helper.ReadNLines(ma, 10);

		ma.close();
		log.close();
		
		long count = lines.stream().filter((String l) -> l.contains("type") && l.contains("cell"))
									.count();
	    
	    return count > 0;
	}
}