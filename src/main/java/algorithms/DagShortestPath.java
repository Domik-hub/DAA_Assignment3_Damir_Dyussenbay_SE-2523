package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public final class DagShortestPath {
    private DagShortestPath() { }
    public record Result(long[] distance, int[] parent, Metrics metrics, Metrics topologicalMetrics) {
        public long sum() { long sum = 0; for (long d : distance) sum = Math.addExact(sum, d); return sum; }
        public List<Integer> pathTo(int v) {
            if (v < 0 || v >= distance.length) throw new IllegalArgumentException("Invalid target");
            List<Integer> path = new ArrayList<>();
            for (int u = v; u != -1; u = parent[u]) path.add(u);
            Collections.reverse(path); return List.copyOf(path);
        }
    }
    public static Result run(Graph g) {
        Kahn.Result topological = Kahn.run(g);
        if (topological.hasCycle()) throw new IllegalArgumentException("Shortest paths require a DAG");
        Metrics metrics = new Metrics(); long[] distance = new long[g.n()]; int[] parent = new int[g.n()];
        Arrays.fill(distance, Long.MAX_VALUE); Arrays.fill(parent, -1);
        for (int v = 0; v < g.n(); v++) if (g.indegree(v) == 0) distance[v] = 0;
        for (int u : topological.order()) {
            for (Edge e : g.neighbors(u)) {
                metrics.edgeScans++; metrics.comparisons++;
                long candidate = distance[u] + (long) e.weight();
                if (candidate < distance[e.to()]) { distance[e.to()] = candidate; parent[e.to()] = u; }
            }
        }
        return new Result(distance, parent, metrics, topological.metrics());
    }
}
