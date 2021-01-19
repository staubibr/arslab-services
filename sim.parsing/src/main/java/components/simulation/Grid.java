package components.simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Grid implements Serializable{

	private static final long serialVersionUID = 4L;

    @JsonProperty("size")
    private int[] size;

    public int[] getSize() {
        return size;
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

    @JsonIgnore 
	private String initialValue;

    public void setInitialValue(String value) {
        this.initialValue = value;
    }
    
    public String getInitialValue() {
        return initialValue;
    }
    
    public Grid() {
        this.size = null;
	    this.initialValue = null;
    }
	
	public List<MessageCA> GlobalFrame(IEmitter emitter) {		
		List<MessageCA> messages = new ArrayList<MessageCA>();
				
		if (this.getInitialValue() == null) return messages;
		
		for (int x = 0; x < this.getSize()[0]; x++) {
			for (int y = 0; y < this.getSize()[1]; y++) {
				for (int z = 0; z < this.getSize()[2]; z++) {
					int[] coord = new int[] { x, y, z };
					
					messages.add(new MessageCA(0, emitter, coord, this.getInitialValue()));
				}
			}
		}
				
		return messages;
	}
}
