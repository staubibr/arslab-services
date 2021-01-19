package parsers.cdpp.celldevs.components;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import components.simulation.IEmitter;
import components.simulation.MessageCA;

public class Grid extends components.simulation.Grid {

	private static final long serialVersionUID = 4L;
    
    @JsonIgnore 
    private List<InitialRowValues> initialRowValues;
	
    public List<InitialRowValues> getInitialRowValues() {
        return initialRowValues;
    }
    
	public Grid() {
		super();
		
	    this.initialRowValues = new ArrayList<InitialRowValues>();
	}

	public List<MessageCA> RowFrame(IEmitter emitter) {	
		List<MessageCA> messages = new ArrayList<MessageCA>();
		
		if (this.getInitialRowValues().size() == 0) return messages;
		
		this.getInitialRowValues().forEach(rv -> {
			for (int y = 0; y < rv.values.size(); y++) {
				int[] coord = new int[] { rv.getRow(), y, 0 };
				String value = rv.getValues().get(y);
				
				messages.add(new MessageCA(0, emitter, coord, value));
			}
		});
		
		return messages;
	}
}
