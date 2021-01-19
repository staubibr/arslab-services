package parsers.cdpp.devs;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import components.parsing.ISimulationParser;
import components.simulation.Link;
import components.simulation.Message;
import components.simulation.Messages;
import components.simulation.Model;
import components.simulation.Port;
import components.simulation.Structure;
import components.simulation.StructureInfo;
import components.utilities.Helper;
import parsers.utilities.MA;

public class SimulationParser implements ISimulationParser {
	
	private static Model current;
	
	@Override
	public void SetInfo(Structure structure, HashMap<String, byte[]> files) {
		String name = Helper.FindName(files, ".ma");
		
		StructureInfo info = new StructureInfo(name, "CDpp", "DEVS");
		
		structure.setInfo(info);
	}

	@Override
	public Structure ParseStructure(HashMap<String, byte[]> files) throws IOException {    	
		Structure structure = new Structure();

		this.SetInfo(structure, files);
		
		InputStream ma = Helper.FindFileStream(files, ".ma");
		
		List<String[]> links = new ArrayList<String[]>();
		
		Helper.ReadFile(ma, (String line) -> {
			String[] lr = line.trim().toLowerCase().split(":");
			String l = lr[0].trim();
			
			if (l.startsWith("[")) {				
				current = new Model(MA.GetModelName(l), Model.Type.ATOMIC, null);
				
				structure.AddModel(current);
			}
			
			if (lr.length < 2) return;
			
			String r = lr[1].trim();

			// components : sender@Sender
			if (l.equals("components")) current.setType(Model.Type.COUPLED);

			// Link : dataOut@sender in1@Network
			else if (l.equals("link")) links.add(MA.ReadLink(current, r));
		});
		
		links.forEach(l -> {
			// For Cell-DEVS models linked to DEVS models, links will contain the coordinate of the 
			// linked cell. For now, we remove that information. In the future, we should keep it.
			// This will require a linkCA class and maybe is a good time to convert parsers as 
			// objects rather than collection of static functions.
			String modelA = l[0].split("\\(")[0];
			String modelB = l[2].split("\\(")[0];
			
			Port start = structure.FindPort(structure.FindNode(modelA), l[1]);
			Port end = structure.FindPort(structure.FindNode(modelB), l[3]);

			if (start == null) {				
				start = structure.AddPort(new Port(structure.FindNode(modelA), l[1], Port.Type.OUTPUT, "[\"out\"]"));
			}

			if (end == null) {
				end = structure.AddPort(new Port(structure.FindNode(modelB), l[3], Port.Type.INPUT, "[\"out\"]"));
			}
			
			// For Cell-DEVS models linked to DEVS models, links will contain the coordinate of the 
			// linked cell. For now, we remove that information. In the future, we should keep it.
			// This will require a linkCA class and maybe is a good time to convert parsers as 
			// objects rather than collection of static functions.
			// link.modelA = link.modelA.split("\\(")[0];
			// link.modelB = link.modelB.split("\\(")[0];
			
			structure.getLinks().add(new Link(start, end));
		});
		
		ma.close();
		
		return structure;
	}

	@Override
	public Messages ParseResults(Structure structure, HashMap<String, byte[]> files) throws IOException {
		InputStream log = Helper.FindFileStream(files, ".log");

		Messages output = new Messages();
		
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
						
			if (!output.getTimesteps().contains(t)) output.getTimesteps().add(t);
			
			Port port = structure.FindPort(m, p);
			
			messages.add(new Message(output.getTimesteps().size() - 1, port, v));
		});

		
		structure.getPorts().sort((Port a, Port b) -> a.getNode().getName().compareTo(b.getNode().getName()));
		structure.getLinks().sort((Link a, Link b) -> a.getStart().getNode().getName().compareTo(b.getStart().getNode().getName()));
		
		structure.ResetIndices();
		
		log.close();
		
		output.setMessages(messages);
		
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
	    
	    return count == 0;
	}
}
