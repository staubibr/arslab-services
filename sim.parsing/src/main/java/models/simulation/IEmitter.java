package models.simulation;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IEmitter {


	@JsonIgnore
	public int getIndex();

	public void setIndex(int value);
	
	public String getName();

    public String getTemplate();

}
