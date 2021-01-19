package components.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageCA extends Message {

	private static final long serialVersionUID = 4L;

	public int[] coord;

	public String getId() {
		return Arrays.stream(this.coord).mapToObj(c -> String.valueOf(c)).collect(Collectors.joining("-"));
	}
	
	public String getTimeId() {
		return String.valueOf(this.getTime()) + "-" + this.getId();
	}
	
    public int[] getCoord() {
        return coord;
    }
    
    public MessageCA(int time, IEmitter emitter, int[] coord, String[] value) {
        super(time, emitter, value);
        
        this.coord = coord;
    }
    
    public MessageCA(int time, IEmitter emitter, int[] coord, String value) {
        super(time, emitter, value);
        
        this.coord = coord;
    }


	public List<String> toArray() {
	    List<String> result = new ArrayList<String>();
	    	    	  
	    result.add(String.valueOf(this.coord[0]));
	    result.add(String.valueOf(this.coord[1]));
	    result.add(String.valueOf(this.coord[2]));
	    
	    // Cell DEVS models always emit through model 0
	    // result.add(String.valueOf(this.getEmitter().getIndex()));
	    
	    Arrays.stream(this.getValue()).forEach(v -> result.add((v != null) ? v : ""));
	
	    return result;
	}
}
