package models.simulation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import parsers.shared.data.InitialRowValues;

@JsonPropertyOrder({ "name", "type", "size" })
public class ModelCA extends Model {

	private static final long serialVersionUID = 4L;

    @JsonIgnore 
	private String initialValue;
    
    @JsonIgnore 
    private List<InitialRowValues> initialRowValues;

    @JsonProperty("size")
    private int[] size;
	
    public String getInitialValue() {
        return initialValue;
    }
    
    public List<InitialRowValues> getInitialRowValues() {
        return initialRowValues;
    }

    public int[] getSize() {
        return size;
    }
	
    public void setInitialValue(String value) {
        this.initialValue = value;
    }

    public void setSize(int[] value) {
        this.size = value;
    }
    
    public void setSizeX(int value) {
    	if (this.size == null) this.size = new int[] { 1,1,1 };
    	
    	this.size[0] = value;
    }
    
    public void setSizeY(int value) {
    	if (this.size == null) this.size = new int[] { 1,1,1 };
    	
    	this.size[1] = value;
    }
    
    public void setSizeZ(int value) {
    	if (this.size == null) this.size = new int[] { 1,1,1 };
    	
    	this.size[2] = value;
    }
    
    public ModelCA(String name, Type type, String template) {
    	super(name, type, template);
    	
        this.initialRowValues = new ArrayList<InitialRowValues>();
        this.initialValue = null;
        this.size = null;
    }
    
    public ModelCA() {
    	
    }
}
