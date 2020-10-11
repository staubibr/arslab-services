package parsers.cadmium.config.celldevs;

public class Port {
	public String name;
	public String type;
	
	public Port(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
}
