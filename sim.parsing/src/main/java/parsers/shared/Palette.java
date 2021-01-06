package parsers.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import components.FilesMap;
import components.Helper;
import models.style.Bucket;
import models.style.LayerStyle;
import models.style.Style;

public class Palette {
	
	public Style ParseTypeA(InputStream pal) throws IOException {		
		Style style = new Style();
		
		style.getLayers().add(new LayerStyle());
		
		LayerStyle layer = style.getLayers().get(0);
		
		// Type A: [rangeBegin;rangeEnd] R G B
		Helper.ReadFile(pal, (String l) -> {			
			if (l == null || l.isEmpty()) return;
			
			if (!l.startsWith("[")) throw new RuntimeException("File format does not correspond to a type A palette.");

			int s = l.indexOf("[");
			int e = l.indexOf("]");
			
			String[] bucket = l.substring(s + 1, e).replaceAll(" ", "").split(";");
						
			float start = Float.parseFloat(bucket[0]);
			float end = Float.parseFloat(bucket[1]);
			
			String[] rgb = l.substring(e + 1).split(" ");
			
			int r = Integer.parseInt(rgb[0]);
			int g = Integer.parseInt(rgb[1]);
			int b = Integer.parseInt(rgb[2]);
			
			layer.getBuckets().add(new Bucket(start, end, new int[] { r,g,b }));
		});
		
		pal.close();
		
		return style;
	}

	public Style ParseTypeB(InputStream pal) throws IOException {
		Style style = new Style();
		
		style.getLayers().add(new LayerStyle());
		
		LayerStyle layer = style.getLayers().get(0);
		
		// Type B (VALIDSAVEFILE: lists R,G,B then lists ranges)
		// TODO: What is VALIDSAVEDFILE??
		ArrayList<float[]> ranges = new ArrayList<float[]>();
		ArrayList<int[]> colors = new ArrayList<int[]>();

		Helper.ReadFile(pal, (String l) -> {		
			if (l == null || l.isEmpty()) return;

			// check number of components per line
			String[] split = l.split(",");		
			
			if(split.length == 2) {
				// this line is a value range [start, end]
				ranges.add(new float[] { Float.parseFloat(split[0]), Float.parseFloat(split[1]) }); 
			}
			else if (split.length == 3){ 
				// this line is a palette element [R,G,B]
				colors.add(new int[] { Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]) }); 
			}
		});
		
		pal.close();
		
		if (ranges.size() != colors.size()) throw new RuntimeException("Ranges and colors length must match in palette type B.");

		// populate grid palette object
		for (int i = 0; i < ranges.size(); i++) {
			layer.getBuckets().add(new Bucket(ranges.get(i)[0], ranges.get(i)[1], new int[] { colors.get(i)[0], colors.get(i)[1], colors.get(i)[2] }));			
		}

		return style;
	}
	
	public Style Parse(FilesMap files) throws IOException {
		InputStream pal = files.FindStream(".pal");
		
		if (pal == null) return null;
		
		List<String> lines = Helper.ReadNLines(pal, 1);

		pal.close();
		
		if (lines.size() < 1) return null;
		
		if (lines.get(0).contains("VALIDSAVEDFILE")) return ParseTypeB(pal); 
		
		else if (lines.get(0).startsWith("[")) return ParseTypeA(pal);
		
		else return null;
	}
}