package parsers.style;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import components.parsing.IStyleParser;
import components.style.Bucket;
import components.style.LayerStyle;
import components.style.Style;
import components.utilities.Helper;

public class PaletteTypeBParser implements IStyleParser {

	public Style ParseStyle(HashMap<String, byte[]> files) throws IOException {
		Style style = new Style();
		
		style.getLayers().add(new LayerStyle());
		
		LayerStyle layer = style.getLayers().get(0);
		
		// Type B (VALIDSAVEFILE: lists R,G,B then lists ranges)
		// TODO: What is VALIDSAVEDFILE??
		ArrayList<float[]> ranges = new ArrayList<float[]>();
		ArrayList<int[]> colors = new ArrayList<int[]>();

		InputStream pal = Helper.FindFileStream(files, ".pal");
		
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

	@Override
	public Boolean Detect(HashMap<String, byte[]> files) throws IOException {
		InputStream pal = Helper.FindFileStream(files, ".pal");
		
		if (pal == null) return false;
		
		List<String> lines = Helper.ReadNLines(pal, 1);

		pal.close();
		
		return (lines.size() == 1 && lines.get(0).contains("VALIDSAVEDFILE")); 
	}
}
