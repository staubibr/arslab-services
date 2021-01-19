package components.style;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import components.utilities.ZipFile;

public class Style {
	private List<LayerStyle> layers;
	
	public List<LayerStyle> getLayers() {
		return this.layers;		
	}
	
	public Style() {
		this.layers = new ArrayList<LayerStyle>();
	}
    
    public byte[] ToBytes() throws JsonProcessingException {
	   	ObjectMapper mapper = new ObjectMapper();
	   	
	   	mapper.setSerializationInclusion(Include.NON_EMPTY); 
	   	
	   	return mapper.writeValueAsBytes(this.getLayers());
    }
    
    public void ToZip(ZipFile zip) throws IOException {
	   	ObjectMapper mapper = new ObjectMapper();
	   	
	   	mapper.setSerializationInclusion(Include.NON_EMPTY); 
	   	
		zip.WriteFull("style.json", mapper.writeValueAsBytes(this.getLayers()));
    }
}
