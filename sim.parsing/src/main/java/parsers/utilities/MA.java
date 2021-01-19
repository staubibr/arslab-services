package parsers.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import components.simulation.Model;
import components.simulation.Port;
import parsers.cdpp.celldevs.components.InitialRowValues;

public class MA {

	public static List<String> GetDefaultIgnoreList() {
		List<String> ignore = new ArrayList<String>();

		ignore.add("top");
		ignore.add("-rule");
		
		return ignore;
	}
	
	public static String GetModelName(String line) {
		return line.substring(1, line.length() - 1);
	}
	
	public static boolean IsIgnored(List<String> ignore, String name) {
		return ignore.stream().filter(i -> name.contains(i)).count() > 0;
	}
	
	public static List<Port> GetNeighborPorts(Model current, String r) {
		return Arrays.stream(r.split(" "))
			  .map(p -> new Port(current, "out_" + p, Port.Type.OUTPUT, null))
			  .collect(Collectors.toList());
	}
	
	public static String GetTemplate(List<Port> ports) {
		List<String> sPorts = ports.stream().map(p -> p.getName()).collect(Collectors.toList());
		
    	ObjectMapper mapper = new ObjectMapper();   	
		
    	String template = "";
    	
		try {
			template = mapper.writeValueAsString(sPorts);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		} 
		
		return template;
	}

	public static String[] ReadLink(Model current, String r) {
		String[] sLink = r.split("\\s+");
		String[] lLink = sLink[0].split("@");
		String[] rLink = sLink[1].split("@");

		String modelA = lLink.length == 1 ? current.getName() : lLink[1];
		String modelB = rLink.length == 1 ? current.getName() : rLink[1];
		
		String portA = lLink[0];
		String portB = rLink[0];
		
		return new String[] { modelA, portA, modelB, portB };
	}

	public static int[] ReadDim(String r) {
		String tmp = r.replaceAll(" ", "");
		
		String[] dim = tmp.substring(1, tmp.length() - 1).split(",|, ");
		
		int[] size = new int[3];
		
		size[0] = Integer.parseInt(dim[0]);
		size[1] = Integer.parseInt(dim[1]);
		size[2] = (dim.length == 2) ? 1 : Integer.parseInt(dim[2]);
		
		return size;
	}

	public static InitialRowValues GetInitialRowValues(String r) {
		
		String[] split = r.split("\\s+");
		
		InitialRowValues rv = new InitialRowValues();
		
		rv.row = Integer.parseInt(split[0]);
		
		for (int i = 0; i < split[1].length(); i++) {
		    char c = split[1].charAt(i);        

			rv.values.add(String.valueOf(c));
		}
		
		return rv;
	}
}