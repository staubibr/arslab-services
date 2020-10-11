package parsers.cadmium.config.devs;

import java.util.ArrayList;
import java.util.List;

public class Ports {

	public List<Port> out;
	public List<Port> in;
	
	public List<Port> getPorts() {
		List<Port> ports = new ArrayList<Port>();

		if (this.in != null) ports.addAll(this.in);

		if (this.out != null) ports.addAll(this.out);
		
		return ports;
	}
}
