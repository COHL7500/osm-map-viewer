package bfst22.vector;

public abstract class AABB {
	public double[] min = new double[]{Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};
	public double[] max = new double[]{Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};

	public void setCoords(double[] coords){
		if(coords[0] < this.min[0]) this.min[0] = coords[0];
		if(coords[1] < this.min[1]) this.min[1] = coords[1];
		if(coords[0] > this.max[0]) this.max[0] = coords[0];
		if(coords[1] > this.max[1]) this.max[1] = coords[1];
	}

	public void setCoords(AABB box){
		this.setCoords(box.min);
		this.setCoords(box.max);
	}
}