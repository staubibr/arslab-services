package parsers.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import components.Helper;
import models.simulation.MessageCA;
import models.simulation.Structure;

public class Val {

	public List<MessageCA> Parse(InputStream val, Structure structure) throws IOException {
		// (0,0,0)=100
		ArrayList<MessageCA> messages = new ArrayList<MessageCA>();
		
		Helper.ReadFile(val, (String l) -> {
			// probably empty line
			if (l == null || l.length() < 4) return;

			if (!l.startsWith("(")) throw new RuntimeException("File format does not correspond to a val file.");
			
			int cI = l.indexOf('('); // coordinate start
			int cJ = l.indexOf(')'); // coordinate end
			int vI = l.indexOf('='); // value start

			String[] c = l.substring(cI + 1, cJ).replaceAll("\\s+", "").split(",");
			
			int[] coord = new int[3]; 

			coord[0] = Integer.parseInt(c[0]);
			coord[1] = Integer.parseInt(c[1]);
			coord[2] = (c.length == 2) ? 0 : Integer.parseInt(c[2]);
			
			String[] values = l.substring(vI + 1).split(" ");
			
			// TODO: This is a mess, particularly when DEVS model are mixed with Cell-DEVS models
			for (int i = 0; i < values.length; i++) {
				messages.add(new MessageCA(0, structure.getPorts().get(i), coord, values[i]));
			}
		});
		
		return messages;
	}
}
