package parsers;

import java.io.IOException;

import components.FilesMap;
import models.simulation.Structure;

public interface ILogParser extends IParser {
	public Structure Parse(FilesMap files) throws IOException;

	public Boolean Validate(FilesMap files) throws IOException;
}
