package components;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.simulation.Structure;
import models.style.Style;

public class Helper {

	public interface LineProcessor {
	    public void process(String line);
	}

	public static byte[] ReadAsBytes(InputStream file) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		file.transferTo(baos);

		return baos.toByteArray();
	}
	
	public static String ReadAllAsString (InputStream file) throws IOException {
		Reader reader = new InputStreamReader(file);
		BufferedReader br = new BufferedReader(reader);
		
		List<String> lines = new ArrayList<String>();
		String line = null;
		
        while ((line = br.readLine()) != null) lines.add(line);
		
		return lines.stream().collect(Collectors.joining(System.lineSeparator()));
	}
	
	public static void ReadFile(InputStream file, LineProcessor delegate) throws IOException {
		Reader reader = new InputStreamReader(file);
		BufferedReader br = new BufferedReader(reader);

		String line = null;
		
        while ((line = br.readLine()) != null) {
        	delegate.process(line);
        }
	}
	
	public static List<String> ReadNLines(InputStream file, int n) throws IOException {
		Reader reader = new InputStreamReader(file);
		BufferedReader br = new BufferedReader(reader);
		
		List<String> lines = new ArrayList<String>();
		
		for (int i = 0; i < n; i++) {
			String line = br.readLine();
		
			if (line == null) break;
			
			lines.add(line);
		}
		
		return lines;
	}

	public static byte[] JsonToByte(Object object) throws JsonProcessingException {
	   	ObjectMapper mapper = new ObjectMapper();
	   	
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

	public static byte[] MakeZip(Structure structure) throws JsonProcessingException, IOException  {
		return MakeZip(structure, null);
	}
	
	public static byte[] MakeZip(Structure structure, Style style) throws JsonProcessingException, IOException {
		ZipFile zip = new ZipFile();
		
		zip.Open();
		
		zip.WriteFull("structure.json", Helper.JsonToByte(structure));
		
		zip.NewEntry("messages.log");
		
		structure.ProcessMessages((String l) -> {
			try {
				byte[] buffer = null;
				
				buffer = StringToByte(l);

				zip.Write(buffer);
			} 
			
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		zip.CloseEntry();
		
		if (style != null) zip.WriteFull("style.json",  Helper.JsonToByte(style.getLayers()));
		
		zip.Close();
		
		return zip.toByteArray();
	}
}
