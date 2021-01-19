package parsers.cadmium.devs.json;

import java.util.ArrayList;
import java.util.List;

public class JsonPorts {

	public List<JsonPort> out;
	public List<JsonPort> in;
	
	public List<JsonPort> getPorts() {
		List<JsonPort> ports = new ArrayList<JsonPort>();

		if (this.in != null) ports.addAll(this.in);

		if (this.out != null) ports.addAll(this.out);
		
		return ports;
	}
}
