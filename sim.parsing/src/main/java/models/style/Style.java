package models.style;

import java.util.ArrayList;
import java.util.List;

public class Style {
	private List<LayerStyle> layers;
	
	public List<LayerStyle> getLayers() {
		return this.layers;		
	}
	
	public Style() {
		this.layers = new ArrayList<LayerStyle>();
	}
}
