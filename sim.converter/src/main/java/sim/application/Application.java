package sim.application;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.HashMap;

import components.parsing.ISimulationParser;
import components.parsing.IStyleParser;
import components.simulation.Messages;
import components.simulation.Structure;
import components.style.Style;
import components.utilities.Helper;
import parsers.utilities.Auto;

public class Application {

	public static void main(String[] args) throws Exception {
		String error = null;
		
		int version = getVersion();
		
		if (version < 8) error = "This application requires a java JDK or JRE 8 or above.";
		
		if (CheckError(error)) return;
		
		if (args.length != 2) error = "This application requires exactly 2 parameters.";
		
		if (CheckError(error)) return;

		final File source = new File(args[0]);
		final File dest = new File(args[1]);
		
		if (!dest.isDirectory()) error = "Second argument must be a path to a folder on your file system. Converted results will be saved in this folder.";
		
		if (!source.isDirectory()) error = "First argument must be a path to a folder on your file system. The folder should contain the simulation outputs only.";
		
		if (CheckError(error)) return;
		
		try {
			System.out.println("reading source files...");
			
			HashMap<String, byte[]> files = GetFilesMap(source);
			
			ISimulationParser parser = Auto.DetectSimulationParser(files);

			if (parser == null) throw new Exception("Unable to automatically detect parser from files.");
						
			System.out.println("converting source files to new specification...");
			
			Structure structure = parser.ParseStructure(files);
			
			Messages messages = parser.ParseResults(structure, files);

			System.out.println("converting palette if provided...");
			
			IStyleParser sParser = Auto.DetectStyleParser(files);
			
			Style style = (sParser == null) ? null : sParser.ParseStyle(files);
			
			System.out.println("zipping everything...");
			
			byte[] zip = Helper.MakeZip(structure, messages, style);
			
			String path = dest + "\\" + structure.getName() + ".zip";

			System.out.println("writing to destination folder...");
			
			FileOutputStream fos = new FileOutputStream(path);
			
			fos.write(zip);
			
			fos.close();

			System.out.println("finished successfully.");
		}
		
		catch (Exception ex) {
			error = ex.getMessage();
		}
		
		finally {
			CheckError(error);
		}
	}
	
	public static Boolean CheckError(String error) {
		if (error == null) return false;
		
		System.out.println(error);

		return true;
	}
	
	public static HashMap<String, byte[]> GetFilesMap(final File folder) throws Exception {
		HashMap<String, byte[]> map = new HashMap<String, byte[]>();
		
		File[] files = folder.listFiles();
		
		if (files.length == 0) throw new Exception("Folder is empty.");
		
	    for (final File file : files) {
	        if (file.isDirectory()) continue;
	        
			byte[] data = Files.readAllBytes(file.toPath());
	        	        	        
	        map.put(file.getName(), data);
	    }
        
	    return map;
	}
	
	private static int getVersion() {
	    String version = System.getProperty("java.version");
	    
	    if(version.startsWith("1.")) version = version.substring(2, 3);
	    
	    else {
	        int dot = version.indexOf(".");
	        
	        if (dot != -1) version = version.substring(0, dot);
	    } 
	    
	    return Integer.parseInt(version);
	}

}
