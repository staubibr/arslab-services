package models.style;

import java.io.Serializable;

public class Bucket implements Serializable {

	private static final long serialVersionUID = 4L;

	public float start;
	public float end;
	public int[] color;

    public float getStart() {
        return start;
    }

    public float getEnd() {
        return end;
    }

    public int[] getColor() {
        return color;
    }
    
    public Bucket(float start, float end, int[] color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }
}
