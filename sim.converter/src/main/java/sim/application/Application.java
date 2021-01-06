package sim.application;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import components.FilesMap;
import components.Helper;
import models.simulation.Structure;
import models.style.Style;
import parsers.IParser;
import parsers.shared.Palette;

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
			
			FilesMap map = GetFilesMap(source);
			
			// map.Mark(0);
			
			IParser parser;
			
			parser = new parsers.auto.Auto();

			System.out.println("converting source files to new specification...");
			
			Structure structure = parser.Parse(map);
			
			Palette palParser = new parsers.shared.Palette();

			System.out.println("converting palette if provided...");
			
			Style style = palParser.Parse(map);
			
			// map.Close();

			System.out.println("zipping everything...");
			
			byte[] zip = Helper.MakeZip(structure, style);
			
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
	
	public static FilesMap GetFilesMap(final File folder) throws Exception {
		FilesMap map = new FilesMap();
		
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
