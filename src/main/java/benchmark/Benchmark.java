package benchmark;

import algorithms.*;
import graph.Graph;
import metrics.Metrics;
import java.nio.file.*;
import java.util.*;

public class Benchmark {
    private static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    private static final long[][] EXPECTED = {
        {147970, 27, 396274}, {30363, 1, 252411},
        {468911, 137, 1271911}, {97589, 1, 723157},
        {1486139, 373, 3941828}, {309550, 1, 2381342},
        {4715515, 1188, 12551891}, {968517, 1, 7376612},
        {15012094, 3923, 40096539}, {3056681, 1, 23512087}
    };

    // Each row stores the counters from one run and the median time of five runs.
    private static void addRow(StringBuilder csv, String name, String kind,
                               Graph graph, long[] times, Metrics metrics, long result) {
        Arrays.sort(times);
        csv.append(String.format(Locale.US, "%s,%s,%d,%d,%.4f,%d,%d,%d,%d,%d%n",
                name, kind, graph.n(), graph.m(), times[2] / 1_000_000.0,
                metrics.dsuOps(), metrics.edgeScans, metrics.comparisons,
                metrics.maxDepth, result));
    }

    private static void check(boolean correct, String message) {
        if (!correct) throw new IllegalStateException(message);
    }

    private static long testMst(int n, int m, String density, long expected, StringBuilder csv) {
        Graph graph = GraphGenerator.generate(n, m, GraphGenerator.Kind.UNDIRECTED);
        long[] kruskalTimes = new long[5];
        long[] primTimes = new long[5];
        MstResult kruskal = null;
        MstResult prim = null;

        for (int run = 0; run < 5; run++) {
            long start = System.nanoTime();
            kruskal = Kruskal.run(graph);
            kruskalTimes[run] = System.nanoTime() - start;

            start = System.nanoTime();
            prim = Prim.run(graph);
            primTimes[run] = System.nanoTime() - start;

            check(kruskal.weight() == prim.weight(), "MST weights do not match");
            check(kruskal.weight() == expected, "Wrong reference MST weight");
            check(kruskal.edges().size() == n - 1 && prim.edges().size() == n - 1, "Wrong tree size");
            check(prim.metrics().edgeScans == 2L * m, "Wrong Prim scan count");
        }
        addRow(csv, "Kruskal", "undirected_" + density, graph, kruskalTimes, kruskal.metrics(), kruskal.weight());
        addRow(csv, "Prim", "undirected_" + density, graph, primTimes, prim.metrics(), prim.weight());
        return kruskal.weight();
    }

    private static long testScc(int n, int m, String density, long expected, StringBuilder csv) {
        Graph graph = GraphGenerator.generate(n, m, GraphGenerator.Kind.DIRECTED);
        long[] times = new long[5];
        SccResult result = null;
        for (int run = 0; run < 5; run++) {
            long start = System.nanoTime();
            result = Kosaraju.run(graph);
            times[run] = System.nanoTime() - start;
            check(result.count() == expected, "Wrong reference SCC count");
            check(result.metrics().edgeScans == 2L * m, "Wrong Kosaraju scan count");
        }
        addRow(csv, "Kosaraju", "directed_" + density, graph, times, result.metrics(), result.count());
        return result.count();
    }

    private static long testDag(int n, int m, String density, long expected, StringBuilder csv) {
        Graph graph = GraphGenerator.generate(n, m, GraphGenerator.Kind.DAG);
        long[] kahnTimes = new long[5];
        long[] pathTimes = new long[5];
        Kahn.Result order = null;
        DagShortestPath.Result paths = null;
        for (int run = 0; run < 5; run++) {
            long start = System.nanoTime();
            order = Kahn.run(graph);
            kahnTimes[run] = System.nanoTime() - start;
            check(!order.hasCycle() && Kahn.validOrder(graph, order.order()), "Wrong topological order");

            start = System.nanoTime();
            paths = DagShortestPath.run(graph);
            pathTimes[run] = System.nanoTime() - start;
            check(paths.sum() == expected, "Wrong reference distance sum");
            check(order.metrics().edgeScans == m && paths.metrics().edgeScans == m, "Wrong DAG scan count");
        }
        addRow(csv, "Kahn", "dag_" + density, graph, kahnTimes, order.metrics(), order.order().length);
        addRow(csv, "DagShortestPath", "dag_" + density, graph, pathTimes, paths.metrics(), paths.sum());
        return paths.sum();
    }

    public static void main(String[] args) throws Exception {
        StringBuilder csv = new StringBuilder(
                "algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result\n");
        int row = 0;
        System.out.println("n,m,MST weight,SCC count,DAG distance sum");

        for (int n : SIZES) {
            for (int factor : new int[]{4, 20}) {
                int m = factor * n;
                String density = factor == 4 ? "sparse" : "dense";
                long[] expected = EXPECTED[row++];

                long mst = testMst(n, m, density, expected[0], csv);
                System.gc();
                long scc = testScc(n, m, density, expected[1], csv);
                System.gc();
                long paths = testDag(n, m, density, expected[2], csv);
                System.gc();

                System.out.printf("%d,%d,%d,%d,%d%n", n, m, mst, scc, paths);
            }
        }
        Files.createDirectories(Path.of("results"));
        Files.writeString(Path.of("results/results.csv"), csv);
        System.out.println("All checks passed. Results saved to results/results.csv");
    }
}
