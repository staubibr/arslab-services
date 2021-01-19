package components.parsing;

import java.io.IOException;
import java.util.HashMap;

import components.style.Style;

public interface IStyleParser {
	public Style ParseStyle(HashMap<String, byte[]> files) throws IOException;

	public Boolean Detect(HashMap<String, byte[]> files) throws IOException;
}