package parsers.style;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import components.parsing.IStyleParser;
import components.style.Bucket;
import components.style.LayerStyle;
import components.style.Style;
import components.utilities.Helper;

public class PaletteTypeAParser implements IStyleParser {

	public Style ParseStyle(HashMap<String, byte[]> files) throws IOException {
		Style style = new Style();
		
		style.getLayers().add(new LayerStyle());
		
		LayerStyle layer = style.getLayers().get(0);
		
		InputStream pal = Helper.FindFileStream(files, ".pal");
		
		// Type A: [rangeBegin;rangeEnd] R G B
		Helper.ReadFile(pal, (String l) -> {			
			if (l == null || l.isEmpty()) return;
			
			if (!l.startsWith("[")) throw new RuntimeException("File format does not correspond to a type A palette.");

			String[] split = l.split(", | |,");

			int r = Integer.parseInt(split[1]);
			int g = Integer.parseInt(split[2]);
			int b = Integer.parseInt(split[3]);
			
			String[] bucket = split[0].substring(1, split[0].length() - 1).split(";");
			
			float start = Float.parseFloat(bucket[0]);
			float end = Float.parseFloat(bucket[1]);
						
			layer.getBuckets().add(new Bucket(start, end, new int[] { r,g,b }));
		});
		
		pal.close();
		
		return style;
	}

	@Override
	public Boolean Detect(HashMap<String, byte[]> files) throws IOException {
		InputStream pal = Helper.FindFileStream(files, ".pal");
		
		if (pal == null) return false;
		
		List<String> lines = Helper.ReadNLines(pal, 1);

		pal.close();
		
		return (lines.size() == 1 && lines.get(0).startsWith("["));
	}
}
