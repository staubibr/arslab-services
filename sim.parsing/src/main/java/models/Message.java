package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Message implements Serializable {

	private static final long serialVersionUID = 4L;

	public String time;
	public String model;
	public String port;
	public String[] value;

    public String getTime() {
        return time;
    }

    public String getModel() {
        return model;
    }

    public String getPort() {
        return port;
    }

    public String[] getValue() {
        return value;
    }
    
    public Message(String time, String model, String port, String[] value) {
        this.time = time;
        this.model = model;
        this.port = port;
        this.value = value;
    }
    
    public Message(String time, String model, String port, String value) {
    	this(time, model, port, new String[] { value });
    }
    
    public Message() {
    	this("","","","");
    }
    
    public List<String> toArray(Structure structure) {
    	List<String> result = new ArrayList<String>();
    	        
        int iP = structure.getPortIndexByMessage(this);
        
        result.add(String.valueOf(iP));
        
        Arrays.stream(this.value).forEach(v -> result.add(v));
        
        return result;
    }
    
    public String toString(Structure structure, CharSequence delimiter) {
    	return this.toArray(structure).stream().collect(Collectors.joining(delimiter));
    }
}
