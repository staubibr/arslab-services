package components.simulation;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import components.utilities.GridSerializer;
import components.utilities.ZipFile;

public class Structure implements Serializable {

	private static final long serialVersionUID = 4L;
	
	private Map<String, String> info;
	private List<Model> nodes;
	private List<Port> ports;
	private List<Link> links;

	@JsonIgnore
	public String getName() {
		return this.getInfo().containsKey("name") ? this.getInfo().get("name") : "unknown";
	}
	
	public Map<String, String> getInfo() {
		return this.info;
	}

	public List<Model> getNodes() {
		return this.nodes;
	}

	public List<Port> getPorts() {
		return this.ports;
	}

	public List<Link> getLinks() {
		return this.links;
	}
	
	public void setInfo(Map<String, String> value) {
		this.info = value;
	}
	
    public Structure(Map<String, String> info, List<Model> models, List<Port> ports, List<Link> links) {
    	this.info = info;
        this.nodes = models;
        this.ports = ports;
        this.links = links;
    }
    
    public Structure() {
    	this.nodes = new ArrayList<Model>();
    	this.ports = new ArrayList<Port>();
    	this.links = new ArrayList<Link>();
    }
    
    public Model AddModel(Model model) {
		model.setIndex(this.getNodes().size());

		this.getNodes().add(model);

		return model;
    }
   
    public Port AddPort(Port port) {    	
		port.setIndex(this.getPorts().size());
		
		this.getPorts().add(port);
		
		return port;
    }
   
    public void AddPorts(List<Port> ports) {    	
		ports.forEach(p -> this.AddPort(p));
    }
   
    public void ResetIndices() {
    	for (int i = 0; i < this.getNodes().size(); i++) {
    		this.getNodes().get(i).setIndex(i);
    	}

    	for (int i = 0; i < this.getPorts().size(); i++) {
    		this.getPorts().get(i).setIndex(i);
    	}
    }
    
    public Model FindNode(String node) {
    	return this.getNodes().stream()
					  		  .filter(n -> n.getName().equals(node))
					  		  .findFirst()
					  		  .orElse(null);
    }
    
    public Port FindPort(Model node, String portName) {
    	return this.getPorts().stream()
					  		  .filter(p -> p.getNode().equals(node) && p.getName().equals(portName))
					  		  .findFirst()
					  		  .orElse(null);
    }
    
    public Port FindPort(String nodeName, String portName) {
    	Model node = FindNode(nodeName);
    	
    	if (node == null) return null;

    	return this.FindPort(node, portName);
    }
    
    public List<Port> FindPorts(Model node) {
    	return this.getPorts().stream().filter(p -> p.getNode().equals(node)).collect(Collectors.toList());
    }
    
    public void ToZip(ZipFile zip) throws IOException {
	   	ObjectMapper mapper = new ObjectMapper();
	   	
	   	SimpleModule module = new SimpleModule();
	   	module.addSerializer(Grid.class, new GridSerializer());
	   	mapper.registerModule(module);
	   	
	   	mapper.setSerializationInclusion(Include.NON_EMPTY); 
	   	
		zip.WriteFull("structure.json", mapper.writeValueAsBytes(this));
    }
}