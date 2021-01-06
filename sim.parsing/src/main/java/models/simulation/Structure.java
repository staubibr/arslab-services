package models.simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Structure implements Serializable {

	public interface LineProcessor {
	    public void process(String line);
	}
	
	private static final long serialVersionUID = 4L;
	
	private Map<String, String> info;
	private List<Model> nodes;
	private List<Port> ports;
	private List<Link> links;
	
	@JsonIgnore
	private List<? extends Message> messages;
	
	@JsonIgnore
	private List<String> timesteps;

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

	public List<? extends Message> getMessages() {
		return messages;
	}
	
	public List<String> getTimesteps() {
		return timesteps;
	}
	
	public void setInfo(Map<String, String> value) {
		this.info = value;
	}
	
	public void setMessages(List<? extends Message> value) {
		this.messages = value;
	}
	
	public void setTimesteps(List<String> value) {
		this.timesteps = value;
	}
	
    public Structure(Map<String, String> info, List<Model> models, List<Port> ports, List<Link> links, List<String> timesteps) {
    	this.info = info;
        this.nodes = models;
        this.ports = ports;
        this.links = links;
        this.timesteps = timesteps;
    }
    
    public Structure() {
    	this.nodes = new ArrayList<Model>();
    	this.ports = new ArrayList<Port>();
    	this.links = new ArrayList<Link>();
    	this.timesteps = new ArrayList<String>();
    }
    
    public Model CreateModel(String name, Model.Type type, String template) {
    	Model model = new Model(name, type, template);
    	
		model.setIndex(this.getNodes().size());

		this.getNodes().add(model);
		
		return model;
    }
    
    public ModelCA CreateModelCA(String name, Model.Type type, String template) {
    	ModelCA model = new ModelCA(name, type, template);
    	
		model.setIndex(this.getNodes().size());

		this.getNodes().add(model);
    	
		return model;
    }
    
    public Port CreatePort(Model model, String name, Port.Type type, String template) {		
		Port port = new Port(model, name, type, template);
    	
		port.setIndex(this.getPorts().size());
		
		this.getPorts().add(port);
		
		return port;
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

	public List<MessageCA> GlobalFrame(Port port) {		
		List<MessageCA> messages = new ArrayList<MessageCA>();
		
		ModelCA model = (ModelCA)port.getNode();
		
		if (model.getInitialValue() == null) return messages;
		
		for (int x = 0; x < model.getSize()[0]; x++) {
			for (int y = 0; y < model.getSize()[1]; y++) {
				for (int z = 0; z < model.getSize()[2]; z++) {
					int[] coord = new int[] { x, y, z };
					
					messages.add(new MessageCA(0, port, coord, model.getInitialValue()));
				}
			}
		}
				
		return messages;
	}

	public List<MessageCA> RowFrame(Port port) {
		List<MessageCA> messages = new ArrayList<MessageCA>();
		
		ModelCA model = (ModelCA)port.getNode();
		
		if (model.getInitialRowValues().size() == 0) return messages;
		
		model.getInitialRowValues().forEach(rv -> {
			for (int y = 0; y < rv.values.size(); y++) {
				int[] coord = new int[] { rv.getRow(), y, 0 };
				String value = rv.getValues().get(y);
				
				messages.add(new MessageCA(0, port, coord, value));
			}
		});
		
		return messages;
	}

	public List<MessageCA> MergeFrames(List<MessageCA> one, List<MessageCA> two) {
		// Man Java sucks (in javascript: var index = {})
		HashMap<String, MessageCA> index = new HashMap<String, MessageCA>();
		
		one.forEach(m -> index.put(m.getId(), m));
	
		two.forEach(m -> {
			String id = m.getId();

			// frame 1 doesn't have message id from frame 2, add it
			if (!index.containsKey(id)) {
				one.add(m);
				
				index.put(id, m);
			}

			// supercede value of 1 by value of 2
			index.get(id).setValue(m.getValue());
		});
				
		return one;
	}
	
	public void ProcessMessages(LineProcessor delegate) {
		// TODO: This could be done in a better way, I just need to get the format out now so I can start writing
		// my paper. A nice way to do this would be to organize messages into frames before reaching this point.		
		List<String> line = new ArrayList<String>();
		
		int t = 0;

		for (Message m : this.getMessages()) {			
			if (m.getTime() != t) {
	        	line.add(0, this.getTimesteps().get(t));

	        	if (line.size() > 1) {
		        	String sLine = line.stream().collect(Collectors.joining(";")) + System.lineSeparator();
		        	
		        	delegate.process(sLine);
	        	}
	        	
				t = m.getTime();
				
				line = new ArrayList<String>();
			}

			line.add(m.toString(","));		
		}

    	line.add(0, this.getTimesteps().get(t));
    	
    	delegate.process(line.stream().collect(Collectors.joining(";")));
	}
}