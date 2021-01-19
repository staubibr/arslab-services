package parsers.lopez.celldevs.components;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import components.simulation.Grid;
import components.simulation.IEmitter;
import components.simulation.Port;

public class ModelCA extends components.simulation.ModelCA {

	private static final long serialVersionUID = 4L;
	
	@JsonIgnore
	private List<Port> ports;
	
	public List<Port> getPorts() {
		return this.ports;
	}
	
	public void setPorts(List<Port> value) {
		this.ports.addAll(value);
	}
	
	public ModelCA(String name, Type type, String template, Grid grid) {
		super(name, type, template, grid);
		
		this.ports = new ArrayList<Port>();

		this.ports.add(0, new Port(this, "out", Port.Type.OUTPUT, "[\"out\"]"));
	}
	
	public int PortIndex(IEmitter port) {
		return this.getPorts().indexOf(port);
	}
}