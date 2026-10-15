package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public final class Kahn {
    private Kahn() { }
    public record Result(int[] order, boolean hasCycle, Metrics metrics) { }
    public static Result run(Graph g) {
        g.requireDirected(); Metrics metrics = new Metrics();
        int[] indegree = g.indegrees(), order = new int[g.n()];
        ArrayDeque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < g.n(); v++) if (indegree[v] == 0) ready.add(v);
        int count = 0;
        while (!ready.isEmpty()) {
            int u = ready.remove(); order[count++] = u;
            for (Edge e : g.neighbors(u)) { metrics.edgeScans++; if (--indegree[e.to()] == 0) ready.add(e.to()); }
        }
        // On a cycle, do not expose a partial sequence as a topological order.
        return new Result(count == g.n() ? order : new int[0], count != g.n(), metrics);
    }
    public static boolean validOrder(Graph g, int[] order) {
        if (!g.directed() || order.length != g.n()) return false;
        int[] position = new int[g.n()]; Arrays.fill(position, -1);
        for (int i = 0; i < order.length; i++) {
            int v = order[i]; if (v < 0 || v >= g.n() || position[v] != -1) return false;
            position[v] = i;
        }
        for (Edge e : g.edges()) if (position[e.from()] >= position[e.to()]) return false;
        return true;
    }
}
