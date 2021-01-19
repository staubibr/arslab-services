package parsers.cadmium.celldevs.json;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonModel {
	public List<Integer> shape;
	public String default_cell_type;
	public Map<String, Object> default_state;
	
	public String getName() {
		return this.default_cell_type;
	}
	
	public int[] getSize() {
		if (shape == null) return null;
		
		int size[] = new int[3];

		size[0] = shape.get(0);
		size[1] = shape.get(1);
		
		size[2] = shape.size() == 3 ? shape.get(2) : 1;
		
		return size;
	}
	
	public List<JsonPort> getPorts() {
		return default_state.keySet().stream()
							  		 .map(ks -> new JsonPort(ks, "output"))
							  		 .collect(Collectors.toList());
	}
}
