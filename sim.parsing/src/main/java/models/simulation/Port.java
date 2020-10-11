package models.simulation;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "model", "name", "type" })
public class Port implements Serializable, IEmitter {
	private static final long serialVersionUID = 4L;
	
	public enum Type { INPUT, OUTPUT }

	@JsonIgnore
	private Model node;

	@JsonIgnore
	private int index;

	@JsonIgnore
	private Type type;

	@JsonIgnore
	private String template;

    @JsonProperty("name")
	private String name;

    @JsonProperty("model")
	public String getNodeIndex() {
        return node.getName();
    }

    @JsonProperty("type")
    public String getTypeString() {
    	return getType().toString().toLowerCase();
    }

    public Type getType() {
        return type;
    }

	@Override
	public int getIndex() {
		return index;
	}
	
	@Override
	public void setIndex(int value) {
		index = value;
	}
    
    public String getName() {
        return name;
    }
    
    public void setName(String value) {
        this.name = value;
    }

    public String getTemplate() {
        return template;
    }
	
	public Model getNode() {
        return node;
    }

    public Port(Model node, String name, Type type, String template) {
		this.node = node;
		this.name = name;
		this.type = type;
		this.template = template;
	}
}
