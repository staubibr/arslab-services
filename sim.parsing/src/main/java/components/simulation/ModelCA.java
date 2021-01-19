package components.simulation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelCA extends Model {

	private static final long serialVersionUID = 4L;

	private Grid grid;
	
	@JsonProperty("size")
	public Grid getGrid() {
		return this.grid;
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
    public ModelCA(String name, Type type, String template, Grid grid) {
        super(name, type, template);
        
        this.grid = grid;
    }
}