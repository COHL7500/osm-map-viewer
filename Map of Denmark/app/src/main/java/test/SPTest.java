package test;

import bfst22.vector.DijkstraSP;
import bfst22.vector.Graph;
import bfst22.vector.Edge;
import java.util.Scanner;

public class SPTest {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int v = s.nextInt();
        int e = s.nextInt();
        Graph g = new Graph(v,e*2);

        for(int i = 0; i < e; i++){
            int from = s.nextInt();
            int to = s.nextInt();
            double property = s.nextDouble();
            g.addEdge(new Edge(from,to ,property));
            g.addEdge(new Edge(to,from, property));
        }
        DijkstraSP sp = new DijkstraSP(g,0);
        System.out.println(sp.pathTo(v-1));


    }
}
