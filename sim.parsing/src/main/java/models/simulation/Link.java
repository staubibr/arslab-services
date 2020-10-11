package models.simulation;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "modelA", "portA", "modelB", "portB" })
public class Link implements Serializable {

	private static final long serialVersionUID = 4L;
	
	@JsonIgnore
	private Port start;
	
	@JsonIgnore
	private Port end;

    public Port getStart() {
        return start;
    }

    public Port getEnd() {
        return end;
    }

    @JsonProperty("modelA")
	public String getModelA() {
		return getStart().getNode().getName();
	}

    @JsonProperty("portA")
	public String getPortA() {
		return getStart().getName();
	}

    @JsonProperty("modelB")
	public String getModelB() {
		return getEnd().getNode().getName();
	}

    @JsonProperty("portB")
	public String getPortB() {
		return getEnd().getName();
	}
    
    public Link(Port start, Port end) {
        this.start = start;
        this.end = end;
    }
}
