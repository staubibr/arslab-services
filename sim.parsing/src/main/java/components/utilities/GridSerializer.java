package components.utilities;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import components.simulation.Grid;

public class GridSerializer extends StdSerializer<Grid> {
    
	private static final long serialVersionUID = 4L;

	public GridSerializer() {
        this(null);
    }
  
    public GridSerializer(Class<Grid> t) {
        super(t);
    }

    @Override
    public void serialize(Grid value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {

    	if (value.getSize() == null) jgen.writeNull();
    	
    	else jgen.writeArray(value.getSize(), 0, value.getSize().length);
    }
}