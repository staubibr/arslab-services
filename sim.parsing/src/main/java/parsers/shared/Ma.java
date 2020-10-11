package parsers.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import components.Helper;
import models.simulation.Link;
import models.simulation.Model;
import models.simulation.ModelCA;
import models.simulation.Port;
import models.simulation.Structure;
import parsers.shared.data.InitialRowValues;

public class Ma {
	
	private static Model current;
	
	private Model ReadModel(Structure structure, String l, String template) {		
		String name = l.substring(1, l.length() - 1);
		
		return structure.CreateModel(name, Model.Type.ATOMIC, template);
	}
	
	private ModelCA ReadModelCA(Structure structure, String l, ArrayList<String> ignore, String template) {
		String name = l.substring(1, l.length() - 1);

		if (ignore.contains(name)) return null; 

		return structure.CreateModelCA(name, Model.Type.COUPLED, template);
	}
	
	private String[] ReadLink(Model current, String r) {
		String[] sLink = r.split("\\s+");
		String[] lLink = sLink[0].split("@");
		String[] rLink = sLink[1].split("@");

		String modelA = lLink.length == 1 ? current.getName() : lLink[1];
		String modelB = rLink.length == 1 ? current.getName() : rLink[1];
		
		String portA = lLink[0];
		String portB = rLink[0];
		
		return new String[] { modelA, portA, modelB, portB };
	}

	private void ReadDim(ModelCA model, String r) {
		String tmp = r.replaceAll(" ", "");
		
		String[] dim = tmp.substring(1, tmp.length() - 1).split(",|, ");
		
		int[] size = new int[3];
		
		size[0] = Integer.parseInt(dim[0]);
		size[1] = Integer.parseInt(dim[1]);
		size[2] = (dim.length == 2) ? 1 : Integer.parseInt(dim[2]);
		
		model.setSize(size);
	}
	
	private void ReadNeighborPorts(Structure structure, Model current, String r, String template) {
		Arrays.stream(r.split(" "))
			  .forEach(p -> structure.CreatePort(current, p, Port.Type.OUTPUT, template));
	}
	
	private void ReadInitialRowValues(ModelCA model, String r) {
		String[] split = r.split("\\s+");
		
		InitialRowValues rv = new InitialRowValues();
		
		rv.row = Integer.parseInt(split[0]);
		
		for (int i = 0; i < split[1].length(); i++) {
		    char c = split[1].charAt(i);        

			rv.values.add(String.valueOf(c));
		}
		
		model.getInitialRowValues().add(rv);
	}

	public Structure Parse(InputStream ma, String template) throws IOException {	
	// public <T extends Model> Structure Parse(InputStream ma, String template) throws IOException {	
		Structure structure = new Structure();
		
		List<String[]> links = new ArrayList<String[]>();
		
		Helper.ReadFile(ma, (String line) -> {
			String[] lr = line.trim().toLowerCase().split(":");
			String l = lr[0].trim();
			
			if (l.startsWith("[")) current = ReadModel(structure, l, template);
			
			if (lr.length < 2) return;
			
			String r = lr[1].trim();

			// components : sender@Sender
			if (l.equals("components")) current.setType(Model.Type.COUPLED);

			// Link : dataOut@sender in1@Network
			else if (l.equals("link")) links.add(ReadLink(current, r));
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
				start = structure.CreatePort(structure.FindNode(modelA), l[1], Port.Type.OUTPUT, template);
			}

			if (end == null) {
				end = structure.CreatePort(structure.FindNode(modelB), l[3], Port.Type.INPUT, template);
			}
			
			// For Cell-DEVS models linked to DEVS models, links will contain the coordinate of the 
			// linked cell. For now, we remove that information. In the future, we should keep it.
			// This will require a linkCA class and maybe is a good time to convert parsers as 
			// objects rather than collection of static functions.
			// link.modelA = link.modelA.split("\\(")[0];
			// link.modelB = link.modelB.split("\\(")[0];
			
			structure.getLinks().add(new Link(start, end));
		});
		
		structure.getPorts().sort((Port a, Port b) -> a.getNode().getName().compareTo(b.getNode().getName()));
		structure.getLinks().sort((Link a, Link b) -> a.getStart().getNode().getName().compareTo(b.getStart().getNode().getName()));
		
		structure.ResetIndices();
		
		return structure;
	}
	
	public Structure ParseCA(InputStream ma, String template) throws IOException {
		Structure structure = new Structure();
				
		ArrayList<String> ignore = new ArrayList<String>();

		ignore.add("top");
		
		Helper.ReadFile(ma, (String line) -> {
			String[] lr = line.trim().toLowerCase().split(":");
			String l = lr[0].trim();
			
			if (l.startsWith("[")) current = ReadModelCA(structure, l, ignore, template);
			
			if (current == null || lr.length < 2) return;
			
			String r = lr[1].trim();
			
			// Link : dataOut@sender in1@Network
			if (l.equals("link")) ReadLink(current, r);

			// NeighborPorts: c ty
			else if (l.equals("neighborports")) ReadNeighborPorts(structure, current, r, template);

			// dim : (30, 30)
			else if (l.equals("dim")) ReadDim((ModelCA)current, r);

			// height : 20
			else if (l.equals("height")) ((ModelCA)current).setSizeX(Integer.parseInt(r));

			// width : 50
			else if (l.equals("width")) ((ModelCA)current).setSizeY(Integer.parseInt(r));

			// initialvalue : 0
			else if (l.equals("initialvalue")) ((ModelCA)current).setInitialValue(r);

			// initialrowvalue :  1      00111011100011100200
			else if (l.equals("initialrowvalue")) ReadInitialRowValues((ModelCA)current, r);

			// localtransition : RegionBehavior
			else if (l.equals("localtransition") || l.equals("zone")) ignore.add(r);
		});

		structure.getPorts().sort((Port a, Port b) -> a.getNode().getName().compareTo(b.getNode().getName()));
		structure.getLinks().sort((Link a, Link b) -> a.getStart().getNode().getName().compareTo(b.getStart().getNode().getName()));

		structure.ResetIndices();
		
		return structure;
	}
}