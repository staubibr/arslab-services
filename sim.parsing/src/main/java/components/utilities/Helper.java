package components.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import components.simulation.Grid;
import components.simulation.MessageCA;
import components.simulation.Messages;
import components.simulation.Structure;
import components.style.Style;

public class Helper {

	public static String FindKey(HashMap<String, byte[]> files, String text) {
		return files.keySet().stream()
				   			.filter(k -> k.toLowerCase().contains(text.toLowerCase()))
							.findFirst()
							.orElse(null);
	}
	
	public static String FindName(HashMap<String, byte[]> files, String text) {
		String key = FindKey(files, text);
		
		return (key == null) ? null : key.substring(0, key.indexOf("."));
	}
	
	public static byte[] FindFileBytess(HashMap<String, byte[]> files, String text) {
		String key = FindKey(files, text);
		
		return (key == null) ? null : files.get(key);
	}
	
	public static InputStream FindFileStream(HashMap<String, byte[]> files, String text) {
		byte[] data = FindFileBytess(files, text);
		
		return (data == null) ? null : new ByteArrayInputStream(data);
	}

	public static List<MessageCA> MergeFrames(List<MessageCA> one, List<MessageCA> two) {
		// Man Java sucks (in javascript: var index = {})
		HashMap<String, MessageCA> index = new HashMap<String, MessageCA>();
		
		one.forEach(m -> index.put(m.getId(), m));
	
		two.forEach(m -> {
			String id = m.getId();

			// frame 1 doesn't have message id from frame 2, add it
			if (!index.containsKey(id)) {
				one.add(m);
				
				index.put(id, m);
			}

			// supercede value of 1 by value of 2
			index.get(id).setValue(m.getValue());
		});
		
		return one;
	}

	public static byte[] JsonToByte(Object object) throws JsonProcessingException {
	   	ObjectMapper mapper = new ObjectMapper();
	   	
	   	SimpleModule module = new SimpleModule();
	   	module.addSerializer(Grid.class, new GridSerializer());
	   	mapper.registerModule(module);
	   	
	   	mapper.setSerializationInclusion(Include.NON_EMPTY); 
	   	
	   	return mapper.writeValueAsBytes(object);
	}
	
	public static byte[] StringToByte(String data) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(output, StandardCharsets.UTF_8);

		osw.write(data);
		osw.close();
		
		return output.toByteArray();
	}
	
	public interface LineProcessor {
	    public void process(String line);
	}
	
	public static void ReadFile(InputStream file, LineProcessor delegate) throws IOException {
		Reader reader = new InputStreamReader(file, "UTF-8");
		BufferedReader br = new BufferedReader(reader);

		String line = null;
		
        while ((line = br.readLine()) != null) {
        	delegate.process(line);
        }
	}
	
	public static List<String> ReadNLines(InputStream file, int n) throws IOException {
		Reader reader = new InputStreamReader(file, "UTF-8");
		BufferedReader br = new BufferedReader(reader);
		
		List<String> lines = new ArrayList<String>();
		
		for (int i = 0; i < n; i++) {
			String line = br.readLine();
		
			if (line != null) lines.add(line);
		}
		
		return lines; 
	}

	public static byte[] MakeZip(Structure structure, Messages messages) throws JsonProcessingException, IOException  {
		return MakeZip(structure, messages, null);
	}
	
	public static byte[] MakeZip(Structure structure, Messages messages, Style style) throws JsonProcessingException, IOException {
		ZipFile zip = new ZipFile();
		
		zip.Open();
		
		structure.ToZip(zip);
		
		messages.ToZip(zip);
		
		if (style != null) style.ToZip(zip);
		
		zip.Close();
		
		return zip.toByteArray();
	}
}