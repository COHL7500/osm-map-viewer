package bfst22.vector;

import java.io.Serializable;
import java.util.*;

public class KdTree implements Serializable, SerialVersionIdentifiable {
	private final List<float[]> splits;
	private final intNode root;
	private List<Node> lines;

	public KdTree(){
		this.root = new intNode();
		this.lines = new ArrayList<>();
		this.splits = new ArrayList<>();
	}

	public void add(final PolyPoint element, Object owner) throws RuntimeException {
		if(this.lines == null) throw new RuntimeException("Unable to add element: KD-Tree already generated!");
		this.lines.add(new Node(element.lat,element.lon,owner));
	}

	public void add(final PolyLine element, Object owner) throws RuntimeException {
		if(this.lines == null) throw new RuntimeException("Unable to add element: KD-Tree already generated!");
		for(int i = 0; i < element.coords.length; i+=2)
			this.lines.add(new Node(element.coords[i],element.coords[i+1],owner));
	}

	public void add(PolyRelation element, Object owner) throws RuntimeException {
		if(this.lines == null) throw new RuntimeException("Unable to add element: KD-Tree already generated!");
		element.parts.forEach(poly -> {
			switch(poly.getClass().getName()){
				case "PolyPoint" -> this.add((PolyPoint) poly, owner);
				case "PolyLine" -> this.add((PolyLine) poly, owner);
				case "PolyRelation" -> this.add((PolyRelation) poly, owner);
			}
		});
	}

	// KD-Tree generic 'Breadth-first search' method
	private void bfs(bfsCall lambda){
		Queue<intNode> nodes = new LinkedList<>();
		nodes.add(this.root);
		int depth = 1;

		while(!nodes.isEmpty()){
			int queueSize = nodes.size();
			depth = depth==0?1:0;

			for(int i = 0; i < queueSize; i++)
				lambda.call(nodes,nodes.remove(),depth);
		}
	}

	public void generateTree() {
		this.root.elements = this.lines;
		this.lines = null;
		this.bfs((q,n,d) -> {
			if (n.elements.size() > 1000) {
				n.elements.sort((o1, o2) -> Float.compare(o1.coords[d], o2.coords[d]));

				n.point = n.elements.get(n.elements.size()/2).coords;
				n.min = n.elements.get(0).coords[d];
				n.max = n.elements.get(n.elements.size()-1).coords[d];

				q.add(n.left = new intNode());
				q.add(n.right = new intNode());

				for (Node node : n.elements) {
					if (node.coords[d==1?0:1] > n.point[d==1?0:1]) n.right.elements.add(node);
					else n.left.elements.add(node);
				}
				n.objects = null;
				n.elements = null;
			} else for(Node e : n.elements) n.objects.add(e.obj);
		});
	}

	public Set<Object> rangeSearch(double[] min, double[] max) {
		Set<Object> allElements = new HashSet<>();
		this.bfs((q,n,d) -> {
			if (n.objects != null) allElements.addAll(n.objects);
			else {
				if (n.min < max[d==1?0:1]) q.add(n.left);
				if (n.max > min[d==1?0:1]) q.add(n.right);
			}
		});
		return allElements;
	}

	public void generateSplits() {
		this.bfs((q,n,d) -> {
			if (n.left != null && n.right != null) {
				this.splits.add(d==1 ? new float[]{n.point[0], n.min} : new float[]{n.min, n.point[1]});
				this.splits.add(d==1 ? new float[]{n.point[0], n.max} : new float[]{n.max, n.point[1]});
				q.add(n.left);
				q.add(n.right);
			}
		});
	}

	public List<float[]> getSplits(){
		return this.splits;
	}

	private Node NNSearch(float[] point, intNode node, Node closest, int depth){
		if(node.objects != null) {
			for (Node obj : node.elements)
				if (obj.distance(point) < closest.distance(point))
					closest = obj;
		} else {
			if(point[(depth+1)%2] < node.point[(depth+1)%2]){
				closest = NNSearch(point, node.left, closest, depth+1);
				if(node.axisDistance(point,(depth+1)%2) < closest.distance(point))
					closest = NNSearch(point, node.right, closest, depth+1);

			} else {
				closest = NNSearch(point, node.right, closest, depth+1);
				if(node.axisDistance(point,(depth+1)%2) < closest.distance(point))
					closest = NNSearch(point, node.left, closest, depth+1);
			}
		}
		return closest;
	}

	public float[] findNN(float[] point){
		return this.NNSearch(point,this.root,new Node(999,999,null),0).coords;
	}

	private interface bfsCall {
		void call(Collection<intNode> queue, intNode node, int depth);
	}

	private static class intNode implements Serializable, SerialVersionIdentifiable {
		public float[] point;
		public float min, max;
		public intNode left, right;
		public List<Node> elements;
		public Set<Object> objects;

		public intNode(){
			this.elements = new ArrayList<>();
			this.objects = new HashSet<>();
		}

		public double distance(float[] point){
			return Math.sqrt(Math.pow(this.point[0]-point[0],2)+Math.pow(this.point[1]-point[1],2));
		}

		public double axisDistance(float[] point, int axis){
			return Math.abs(point[axis]-this.point[axis]);
		}
	}

	private static class Node implements Serializable, SerialVersionIdentifiable {
		public float[] coords;
		public Object obj;

		public Node(float lat, float lon, Object objRef) {
			this.coords = new float[]{lat, lon};
			this.obj = objRef;
		}

		public double distance(float[] point){
			return Math.sqrt(Math.pow(this.coords[0]-point[0],2)+Math.pow(this.coords[1]-point[1],2));
		}
	}
}