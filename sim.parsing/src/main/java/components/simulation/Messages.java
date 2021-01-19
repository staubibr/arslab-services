package components.simulation;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import components.parsing.IMessage;
import components.utilities.Helper;
import components.utilities.ZipFile;

public class Messages implements Serializable {
	public interface LineProcessor {
	    public void process(String line);
	}
	
	private static final long serialVersionUID = 4L;

	@JsonIgnore
	private List<? extends Message> messages;

	public List<? extends Message> getMessages() {
		return messages;
	}

	public void setMessages(List<? extends Message> messages) {
		this.messages = messages;
	}
	
	@JsonIgnore
	private List<String> timesteps;

	public List<String> getTimesteps() {
		return timesteps;
	}
	
	public void setTimesteps(List<String> value) {
		this.timesteps = value;
	}
		
    public Messages() {
        this.timesteps = new ArrayList<String>();
        this.messages = null;
    }
	
	public void ToZip(ZipFile zip) throws IOException {
		zip.NewEntry("messages.log");
		
		int t = -1;
		String s = System.lineSeparator();
		
		for (IMessage m : this.getMessages()) {
			if (m.getTime() != t) {
				t = m.getTime();

				zip.Write(Helper.StringToByte(this.getTimesteps().get(t) + s));
			}

			zip.Write(Helper.StringToByte(m.toString(",") + s));
		}

		zip.CloseEntry();
	}
}