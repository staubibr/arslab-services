package components.parsing;

import java.io.Serializable;
import java.util.List;

import components.simulation.IEmitter;

public interface IMessage extends Serializable{

    public int getTime();

    public IEmitter getEmitter();

    public String[] getValue();
    
    public void setValue(String[] value);
    
	public List<String> toArray();
	
	public String toString(CharSequence delimiter);
}
