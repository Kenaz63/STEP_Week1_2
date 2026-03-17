import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class WebsiteTraffic {
    private final ConcurrentHashMap<String, AtomicInteger> pageViews = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> sourceCount = new ConcurrentHashMap<>();

    public void processEvent(String url, String userId, String source) {
        pageViews.computeIfAbsent(url, k -> new AtomicInteger()).incrementAndGet();
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);
        sourceCount.computeIfAbsent(source, k -> new AtomicInteger()).incrementAndGet();
    }

    public List<String> getTopPages() {
        PriorityQueue<Map.Entry<String, AtomicInteger>> pq =
                new PriorityQueue<>(Comparator.comparingInt(e -> e.getValue().get()));

        for (Map.Entry<String, AtomicInteger> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 10) pq.poll();
        }

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            Map.Entry<String, AtomicInteger> e = pq.poll();
            String url = e.getKey();
            int views = e.getValue().get();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
            result.add(url + " - " + views + " views (" + unique + " unique)");
        }
        Collections.reverse(result);
        return result;
    }

    public Map<String, Integer> getSourceStats() {
        Map<String, Integer> res = new HashMap<>();
        for (Map.Entry<String, AtomicInteger> e : sourceCount.entrySet()) {
            res.put(e.getKey(), e.getValue().get());
        }
        return res;
    }
}