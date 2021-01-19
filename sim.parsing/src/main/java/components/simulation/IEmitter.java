package components.simulation;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IEmitter {

	@JsonIgnore
	public int getIndex();

	public void setIndex(int value);
	
	public String getName();
	
	public void setName(String value);

    public String getTemplate();

    public void setTemplate(String value);
}
