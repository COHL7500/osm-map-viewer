package bfst22.vector;

import java.io.Serializable;
import java.util.*;

public class KdTree implements Serializable, SerialVersionIdentifiable {
	private final intNode root;
	private List<Node> lines;

	public KdTree(){
		this.root = new intNode();
		this.lines = new ArrayList<>();
	}

	public void add(PolyLine element) throws RuntimeException {
		if(this.lines == null) throw new RuntimeException("Unable to add element: KD-Tree already generated!");
		for(int i = 0; i < element.coords.length; i+=2)
			this.lines.add(new Node(element.coords[i],element.coords[i+1],element));
	}

	public void add(MultiPolygon element) throws RuntimeException {
		if(this.lines == null) throw new RuntimeException("Unable to add element: KD-Tree already generated!");
		element.parts.forEach(polyline -> {
			for(int i = 0; i < ((PolyLine) polyline).coords.length; i+=2)
				this.lines.add(new Node(((PolyLine) polyline).coords[i], ((PolyLine) polyline).coords[i+1], element));
		});
	}

	// KD-Tree generic 'Breadth-first search' method
	private void bfs(bfsCall lambda, Collection array){
		Queue<intNode> intNodes = new LinkedList<>();
		boolean depth = true;
		intNodes.add(this.root);

		while(!intNodes.isEmpty()){
			int queueSize = intNodes.size();
			depth = !depth;

			for(int i = 0; i < queueSize; i++){
				intNode lnode = intNodes.remove();
				lambda.call(intNodes,lnode,depth,array);
			}
		}
	}

	public void generate(){
		this.root.elements = this.lines;
		bfsCall kd = (q,n,d,a) -> {
			if(n.elements.size() > 1000) {
				for (Node k : n.elements) k.setCompareAxis(d?1:0);
				Collections.sort(n.elements);

				n.point = n.elements.get(n.elements.size()/2).coords;
				n.min   = n.elements.get(0).coords[d?1:0];
				n.max   = n.elements.get(n.elements.size()-1).coords[d?1:0];

				q.add(n.left = new intNode());
				q.add(n.right = new intNode());

				for (Node node : n.elements) {
					if (node.coords[d?0:1] > n.point[d?0:1]) n.right.elements.add(node);
					else n.left.elements.add(node);
				}

				n.elements = null;
			} else {
				n.objects = new HashSet<>();
				for(Node node : n.elements) n.objects.add(node.obj);
				n.elements = null;
			}
		};
		this.bfs(kd,null);
		this.lines = null;
	}

	public Set<Drawable> rangeSearch(double[] min, double[] max){
		Set<Drawable> allElements = new HashSet<>();
		bfsCall rs = (q,n,d,a) -> {
			if(a != null && (n.left == null || n.right == null)) a.addAll(n.objects);
			else {
				if(n.min < max[d?0:1]) q.add(n.left);
				if(n.max > min[d?0:1]) q.add(n.right);
			}
		};
		this.bfs(rs,allElements);
		return allElements;
	}

	public List<float[]> getSplit(){
		List<float[]> lines = new ArrayList<>();
		bfsCall gs = (q,n,d,a) -> {
			if(n.left != null || n.right != null){
				lines.add(d ? new float[]{n.point[0],n.min} : new float[]{n.min,n.point[1]});
				lines.add(d ? new float[]{n.point[0],n.max} : new float[]{n.max,n.point[1]});
				q.add(n.left);
				q.add(n.right);
			}
		};
		this.bfs(gs,lines);
		return lines;
	}

	/*public List<Node> NNSearch(){

	}*/

	private interface bfsCall {
		void call(Queue<intNode> queue, intNode node, boolean depth, Collection array);
	}

	private static class intNode implements Serializable, SerialVersionIdentifiable {
		public float[] point;
		public float min, max;
		public intNode left, right;
		public List<Node> elements = new ArrayList<>();
		public Set<Drawable> objects = new HashSet<>();
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