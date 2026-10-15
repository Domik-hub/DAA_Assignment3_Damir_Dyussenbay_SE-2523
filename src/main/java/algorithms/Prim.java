package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public final class Prim {
    private Prim() { }
    public static MstResult run(Graph g) {
        g.requireUndirected(); Metrics metrics = new Metrics();
        boolean[] visited = new boolean[g.n()]; List<Edge> chosen = new ArrayList<>();
        PriorityQueue<Edge> queue = new PriorityQueue<>((a, b) -> {
            metrics.comparisons++;
            int c = Integer.compare(a.weight(), b.weight());
            if (c == 0) c = Integer.compare(a.from(), b.from());
            if (c == 0) c = Integer.compare(a.to(), b.to());
            return c;
        });
        int components = 0; long weight = 0;
        for (int start = 0; start < g.n(); start++) {
            if (visited[start]) continue;
            components++; visited[start] = true;
            offerOutgoing(g, start, visited, queue, metrics);
            while (!queue.isEmpty()) {
                Edge e = queue.remove();
                if (visited[e.to()]) continue;
                visited[e.to()] = true; chosen.add(e); weight += e.weight();
                offerOutgoing(g, e.to(), visited, queue, metrics);
            }
        }
        return new MstResult(weight, chosen, components, metrics);
    }
    private static void offerOutgoing(Graph g, int u, boolean[] visited, PriorityQueue<Edge> q, Metrics m) {
        for (Edge e : g.neighbors(u)) { m.edgeScans++; if (!visited[e.to()]) q.add(e); }
    }
}
