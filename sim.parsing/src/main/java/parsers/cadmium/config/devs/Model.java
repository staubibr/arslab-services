package parsers.cadmium.config.devs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Model {

	public String id;
	public String type;
	public Ports ports;
	public List<Coupling> ic;
	public List<Coupling> eoc;
	public List<Coupling> eic;
	public List<Model> models;

	
	public String getName() {
		return this.id;
	}
	
	public List<Port> getPorts() {
		return this.ports.getPorts();
	}
		
	public List<String> getSubmodels() {
		if (models == null) return new ArrayList<String>();
		
		return models.stream().map(m -> m.getName()).collect(Collectors.toList());
	}
	
	public List<Coupling> getCouplings() {
		List<Coupling> couplings = new ArrayList<Coupling>();

		if (this.ic != null) couplings.addAll(this.ic);

		if (this.eoc != null) couplings.addAll(this.eoc);

		if (this.eic != null) couplings.addAll(this.eic);
		
		return couplings;
	}
	
	public interface ModelProcessor {
	    public void process(Model model);
	}

	public void Traverse(ModelProcessor delegate) {
		delegate.process(this);

		if (models == null) return;
		
		models.forEach(m -> m.Traverse(delegate));
	}
}
