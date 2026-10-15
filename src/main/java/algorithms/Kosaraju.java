package algorithms;

import graph.*;
import metrics.Metrics;
import java.util.*;

public final class Kosaraju {
    private Kosaraju() { }
    public static SccResult run(Graph g) {
        g.requireDirected(); Metrics metrics = new Metrics(); int n = g.n();
        boolean[] seen = new boolean[n]; int[] stack = new int[n], cursor = new int[n], finish = new int[n];
        int finished = 0;
        for (int root = 0; root < n; root++) {
            if (seen[root]) continue;
            int depth = 1; stack[0] = root; seen[root] = true; metrics.depth(depth);
            while (depth > 0) {
                int u = stack[depth - 1]; List<Edge> out = g.neighbors(u);
                if (cursor[u] < out.size()) {
                    Edge e = out.get(cursor[u]++); metrics.edgeScans++;
                    if (!seen[e.to()]) { seen[e.to()] = true; stack[depth++] = e.to(); metrics.depth(depth); }
                } else { depth--; finish[finished++] = u; }
            }
        }
        Graph reversed = g.reversed(); int[] component = new int[n]; Arrays.fill(component, -1);
        Arrays.fill(cursor, 0); int count = 0;
        for (int i = finished - 1; i >= 0; i--) {
            int root = finish[i]; if (component[root] != -1) continue;
            int depth = 1; stack[0] = root; component[root] = count; metrics.depth(depth);
            while (depth > 0) {
                int u = stack[depth - 1]; List<Edge> out = reversed.neighbors(u);
                if (cursor[u] < out.size()) {
                    Edge e = out.get(cursor[u]++); metrics.edgeScans++;
                    if (component[e.to()] == -1) { component[e.to()] = count; stack[depth++] = e.to(); metrics.depth(depth); }
                } else depth--;
            }
            count++;
        }
        return new SccResult(count, component, metrics);
    }
}
