package algorithms;

import graph.Edge;
import metrics.Metrics;
import java.util.List;

public record MstResult(long weight, List<Edge> edges, int components, Metrics metrics) {
    public MstResult { edges = List.copyOf(edges); }
}
