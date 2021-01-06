package models.simulation;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "type", "template" })
public class Model implements Serializable, IEmitter {
	private static final long serialVersionUID = 4L;

	public enum Type { ATOMIC, COUPLED }

	@JsonIgnore
	private int index;

	@JsonIgnore
	private Type type;

	@JsonIgnore
	private String template;

    @JsonProperty("name")
	private String name;

    @JsonProperty("type")
    public String getTypeString() {
    	return this.getType().toString().toLowerCase();
    }
    
	public Type getType() {
		return type;
	}

	public void setType(Type value) {
		this.type = value;
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

    @JsonProperty("template")
    public String getTemplate() {
        return template;
    }
	
    public void setTemplate(String value) {
        this.template = value;
    }

    public Model(String name, Type type, String template) {
        this.name = name;
        this.type = type;
        this.template = template;
    }
    
    public Model() {
    	
    }
}
