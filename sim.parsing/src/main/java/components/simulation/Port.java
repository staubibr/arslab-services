package components.simulation;

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

	private String template;

	@JsonIgnore
	private int templateSize;

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
	
	public Model getNode() {
        return node;
    }

	@Override
	public int getIndex() {
		return index;
	}
	
	@Override
	public void setIndex(int value) {
		index = value;
	}

	@Override
    public String getName() {
        return name;
    }

	@Override
    public void setName(String value) {
        this.name = value;
    }

	@Override
    public String getTemplate() {
        return template;
    }

	@Override
	public void setTemplate(String value) {
		this.template = value;		
	}

    public Port(Model node, String name, Type type, String template) {
		this.node = node;
		this.name = name;
		this.type = type;
		this.template = template;
	}
}
