package algorithms;

import metrics.Metrics;

public record SccResult(int count, int[] componentOf, Metrics metrics) { }
