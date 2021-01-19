package components.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import components.parsing.IMessage;

public class Message implements IMessage {

	private static final long serialVersionUID = 4L;

	private int time;
	private IEmitter emitter;
	private String[] value;

    public int getTime() {
        return time;
    }

    public IEmitter getEmitter() {
        return emitter;
    }

    public String[] getValue() {
        return value;
    }
    
    public void setValue(String[] value) {
    	this.value = value;
    }
    
    public Message(int time, IEmitter emitter, String[] value) {
        this.time = time;
        this.emitter = emitter;
        this.value = value;
    }
    
    public Message(int time, IEmitter emitter, String value) {
    	this(time, emitter, new String[] { value });
    }

	public List<String> toArray() {
		List<String> result = new ArrayList<String>();
					    
	    result.add(String.valueOf(this.getEmitter().getIndex()));
	    
	    Arrays.stream(this.getValue()).forEach(v -> result.add((v != null) ? v : ""));

	    return result;
	}
	
	public String toString(CharSequence delimiter) {
		return this.toArray().stream().collect(Collectors.joining(delimiter));
	}
}
