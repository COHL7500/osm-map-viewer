package bfst22.vector;

import java.util.*;

public class KdTree {
	private intNode root;

	public KdTree(List<Node> nodes){
		this.generate(nodes);
	}

	private void generate(List<Node> nodes){
		Queue<intNode> intNodes = new LinkedList<>();
		boolean depth = true;

		this.root = new intNode();
		this.root.elements = nodes;
		intNodes.add(this.root);

		while(!intNodes.isEmpty()){
			int queueSize = intNodes.size();
			depth = !depth;

			for(int i = 0; i < queueSize; i++){
				intNode lnode = intNodes.remove();

				if(lnode.elements.size() > 1000) {
					for (Node k : lnode.elements)
						k.setCompareAxis(depth ? 1 : 0);
					Collections.sort(lnode.elements);

					lnode.point = lnode.elements.get((int) lnode.elements.size() / 2).coords[depth ? 1 : 0];
					intNodes.add(lnode.left = new intNode());
					intNodes.add(lnode.right = new intNode());

					for (Node node : lnode.elements) {
						if (node.coords[depth ? 1 : 0] < lnode.point) lnode.right.elements.add(node);
						else lnode.left.elements.add(node);
					}

					lnode.elements = null;
				}
			}
		}
	}

	public Set<Drawable> rangeSearch(double[] min, double[] max){
		Queue<intNode> intNodes = new LinkedList<>();
		Set<Drawable> allElements = new HashSet<>();
		int depth = -1;
		intNodes.add(this.root.left);
		intNodes.add(this.root.right);

		while(!intNodes.isEmpty()){
			int queueSize = intNodes.size();
			depth++;

			for(int i = 0; i < queueSize; i++){
				intNode lnode = intNodes.remove();

				if(lnode.left == null){
					for(Node node : lnode.elements)
						allElements.add(node.obj);
				} else if(lnode.left.left == null && lnode.point < min[depth%2]){
					intNodes.add(lnode.left);
				} else if(lnode.right.left == null && lnode.point < min[depth%2]){
					intNodes.add(lnode.right);
				} else {
					intNodes.add(lnode.left);
					intNodes.add(lnode.right);
				}
			}
		}

		return allElements;
	}

	/*public List<Node> NNSearch(){

	}*/

	private static class intNode {
		public float point;
		public intNode left, right;
		public List<Node> elements = new ArrayList<>();
	}
}