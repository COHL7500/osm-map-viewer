package bfst22.vector;

import java.io.Serializable;
import java.util.*;

public class KdTree implements Serializable, SerialVersionIdentifiable {
	private final intNode root = new intNode();
	private final List<Node> lines = new ArrayList<>();

	public void add(Drawable element){
		if(element instanceof PolyLine) this.add((PolyLine) element);
		else this.add((MultiPolygon) element);
	}

	public void add(PolyLine element){
		for(int i = 0; i < element.coords.length; i+=2)
			this.lines.add(new Node(element.coords[i],element.coords[i+1],element));
	}

	public void add(MultiPolygon element){
		element.parts.forEach(polyline -> {
			for(int i = 0; i < ((PolyLine) polyline).coords.length; i+=2)
				this.lines.add(new Node(((PolyLine) polyline).coords[i], ((PolyLine) polyline).coords[i+1], element));
		});
	}

	public void generate(float minlat, float minlon, float maxlat, float maxlon){
		Queue<intNode> intNodes = new LinkedList<>();
		int depth = -1;

		this.root.size = new float[][]{new float[]{minlon,maxlat},new float[]{maxlon,minlat}};
		this.root.elements = this.lines;
		intNodes.add(this.root);

		while(!intNodes.isEmpty()){
			int queueSize = intNodes.size();
			depth++;

			for(int i = 0; i < queueSize; i++){
				intNode lnode = intNodes.remove();

				if(lnode.elements.size() > 1000) {
					for (Node k : lnode.elements)
						k.setCompareAxis(depth%2);
					Collections.sort(lnode.elements);

					lnode.point = lnode.elements.get((int) lnode.elements.size() / 2).coords[depth%2];
					intNodes.add(lnode.left = new intNode());
					intNodes.add(lnode.right = new intNode());

					//lnode.left.size = lnode.size;
					//lnode.left.size[(depth+1)%2][depth%2] = lnode.point;
					//lnode.right.size = lnode.size;
					//lnode.right.size[depth%2][depth%2] = lnode.point;

					for (Node node : lnode.elements) {
						if (node.coords[depth%2] < lnode.point) lnode.right.elements.add(node);
						else lnode.left.elements.add(node);
					}

					//lnode.left.parent = lnode.right.parent = lnode;
					lnode.elements = null;
				}
			}
		}
	}

	public Set<Drawable> rangeSearch(double[] min, double[] max){
		if(this.root.left != null && this.root.right != null){
			Queue<intNode> intNodes = new LinkedList<>();
			Set<Drawable> allElements = new HashSet<>();
			boolean depth = true;

			intNodes.add(this.root);

			while(!intNodes.isEmpty()){
				int queueSize = intNodes.size();
				depth = !depth;

				for(int i = 0; i < queueSize; i++){
					intNode lnode = intNodes.remove();

					if(lnode.left == null || lnode.right == null){
						for(Node node : lnode.elements)
							if(node.coords[0] >= min[1] && node.coords[0] <= max[1] && node.coords[1] >= min[0] && node.coords[1] <= max[0])
								allElements.add(node.obj);
					} else {
						intNodes.add(lnode.left);
						intNodes.add(lnode.right);
					}
				}
			}

			return allElements;
		}

		return new HashSet<>();
	}

	public List<float[][]> getSplit(){
		if(this.root.left != null && this.root.right != null){
			Queue<intNode> intNodes = new LinkedList<>();
			List<float[][]> lines = new ArrayList<>();
			boolean depth = true;

			intNodes.add(this.root.left);
			intNodes.add(this.root.right);

			while(!intNodes.isEmpty()){
				int queueSize = intNodes.size();
				depth = !depth;

				for(int i = 0; i < queueSize; i++){
					intNode lnode = intNodes.remove();
					if(depth) lines.add(new float[][]{{lnode.size[0][0],lnode.parent.point},{lnode.size[0][1],lnode.parent.point}});
					else lines.add(new float[][]{{lnode.parent.point,lnode.size[1][0]},{lnode.parent.point,lnode.size[1][1]}});
					if(lnode.left != null){
						intNodes.add(lnode.left);
						intNodes.add(lnode.right);
					}
				}
			}

			return lines;
		}

		return new ArrayList<>();
	}

	/*public List<Node> NNSearch(){

	}*/

	private static class intNode {
		public float point;
		public float[][] size = new float[2][2];
		public intNode left, right, parent;
		public List<Node> elements = new ArrayList<>();
	}

	private static class Node implements Comparable<Node> {
		public float[] coords;
		public Drawable obj;
		private int compareAxis;

		public Node(float lat, float lon, Drawable objRef){
			this.coords = new float[]{lat,lon};
			this.obj = objRef;
			this.compareAxis = 0;
		}

		public void setCompareAxis(int i){
			this.compareAxis = i;
		}

		@Override public int compareTo(Node node){
			return Float.compare(this.coords[this.compareAxis], node.coords[this.compareAxis]);
		}
	}
}