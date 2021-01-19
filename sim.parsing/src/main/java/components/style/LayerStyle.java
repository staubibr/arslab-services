package components.style;

import java.util.ArrayList;
import java.util.List;

public class LayerStyle  {
	private List<Bucket> buckets;

	public List<Bucket> getBuckets() {
		return this.buckets;
	}
	
	public LayerStyle() {
		this.buckets = new ArrayList<Bucket>();
	}
}
	
