package parsers.cadmium.devs.json;

public class JsonCoupling {
	public String from_model = null;
	public String from_port;
	public String to_model = null;
	public String to_port;

	public void setModel(String value) {
		if (from_model == null) from_model = value;

		if (to_model == null) to_model = value;
	}
	
	public String getModelA() {
		return this.from_model;
	}
	
	public String getPortA() {
		return this.from_port;
	}

	public String getModelB() {
		return this.to_model;
	}
	
	public String getPortB() {
		return this.to_port;
	}
}
