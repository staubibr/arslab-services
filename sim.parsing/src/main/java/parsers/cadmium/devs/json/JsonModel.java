package parsers.cadmium.devs.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonModel {

	public String id;
	public String type;
	public JsonPorts ports;
	public List<JsonCoupling> ic;
	public List<JsonCoupling> eoc;
	public List<JsonCoupling> eic;
	public List<JsonModel> models;

	
	public String getName() {
		return this.id;
	}
	
	public List<JsonPort> getPorts() {
		return this.ports.getPorts();
	}
		
	public List<String> getSubmodels() {
		if (models == null) return new ArrayList<String>();
		
		return models.stream().map(m -> m.getName()).collect(Collectors.toList());
	}
	
	public List<JsonCoupling> getCouplings() {
		List<JsonCoupling> couplings = new ArrayList<JsonCoupling>();

		if (this.ic != null) couplings.addAll(this.ic);

		if (this.eoc != null) couplings.addAll(this.eoc);

		if (this.eic != null) couplings.addAll(this.eic);
		
		return couplings;
	}
	
	public interface ModelProcessor {
	    public void process(JsonModel model);
	}

	public void Traverse(ModelProcessor delegate) {
		delegate.process(this);

		if (models == null) return;
		
		models.forEach(m -> m.Traverse(delegate));
	}
}
