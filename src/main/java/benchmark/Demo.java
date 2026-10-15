package benchmark;

import algorithms.*;
import graph.Graph;
import java.nio.file.Path;
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: Demo data/graph.txt");
            return;
        }

        Graph graph = Graph.read(Path.of(args[0]));
        if (!graph.directed()) {
            MstResult kruskal = Kruskal.run(graph);
            MstResult prim = Prim.run(graph);
            System.out.println("Kruskal weight: " + kruskal.weight());
            System.out.println("Prim weight: " + prim.weight());
            System.out.println("Components: " + kruskal.components());
            return;
        }

        System.out.println("SCC count: " + Kosaraju.run(graph).count());
        Kahn.Result order = Kahn.run(graph);
        if (order.hasCycle()) {
            System.out.println("The graph contains a cycle.");
            return;
        }

        System.out.println("Order: " + Arrays.toString(order.order()));
        DagShortestPath.Result paths = DagShortestPath.run(graph);
        for (int v = 0; v < graph.n(); v++) {
            System.out.println(v + ": distance = " + paths.distance()[v]
                    + ", path = " + paths.pathTo(v));
        }
    }
}
