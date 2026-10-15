package metrics;

/** Actual event counters. A scan is an algorithm examining an adjacency/input edge. */
public final class Metrics {
    public long findCalls, successfulUnions, edgeScans, comparisons;
    public int maxDepth;
    public long dsuOps() { return findCalls + successfulUnions; }
    public long operations() { return dsuOps() + edgeScans + comparisons; }
    public void depth(int depth) { maxDepth = Math.max(maxDepth, depth); }
    public String signature() {
        return findCalls + ":" + successfulUnions + ":" + edgeScans + ":" + comparisons + ":" + maxDepth;
    }
}
