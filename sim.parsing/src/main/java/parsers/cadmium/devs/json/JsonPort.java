package parsers.cadmium.devs.json;

public class JsonPort {

	public String name;
	public String message_type;
	public String port_kind;

	public String getType() {
		if (this.port_kind.equals("out")) return "output";
		
		if (this.port_kind.equals("in")) return "input";
		
		throw new Error("Unrecognized type for port.");
	}
	
	public String getName() {
		return this.name;
	}
	
}
