package parsers.cadmium.celldevs.json;

public class JsonPort {
	public String name;
	public String type;
	
	public JsonPort(String name, String type) {
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
