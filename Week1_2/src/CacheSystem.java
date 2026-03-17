import java.util.*;

public class CacheSystem {

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true); // access-order
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private LRUCache<String, String> L1;
    private LRUCache<String, String> L2;
    private Map<String, String> L3; // DB simulation

    private Map<String, Integer> accessCount;

    private static final int PROMOTION_THRESHOLD = 3;

    public CacheSystem(int l1Size, int l2Size) {
        L1 = new LRUCache<>(l1Size);
        L2 = new LRUCache<>(l2Size);
        L3 = new HashMap<>();
        accessCount = new HashMap<>();
    }

    public String getVideo(String videoId) {

        // L1
        if (L1.containsKey(videoId)) {
            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
            return "L1 HIT: " + L1.get(videoId);
        }

        // L2
        if (L2.containsKey(videoId)) {
            String data = L2.get(videoId);
            promoteToL1(videoId, data);
            return "L2 HIT → Promoted to L1";
        }

        // L3 (DB)
        String data = L3.getOrDefault(videoId, "DB_DATA_" + videoId);
        L3.put(videoId, data);

        L2.put(videoId, data); // load into L2
        return "L3 HIT → Loaded into L2";
    }

    private void promoteToL1(String key, String value) {
        int count = accessCount.getOrDefault(key, 0) + 1;
        accessCount.put(key, count);

        if (count >= PROMOTION_THRESHOLD) {
            L1.put(key, value);
        }
    }

    public void putVideo(String videoId, String data) {
        L3.put(videoId, data);
    }

    public void invalidate(String videoId) {
        L1.remove(videoId);
        L2.remove(videoId);
        L3.remove(videoId);
        accessCount.remove(videoId);
    }

    public void stats() {
        System.out.println("L1 size: " + L1.size());
        System.out.println("L2 size: " + L2.size());
        System.out.println("L3 size: " + L3.size());
    }

    // Demo
    public static void main(String[] args) {
        CacheSystem cache = new CacheSystem(2, 3);

        cache.putVideo("video_123", "Cool Movie");

        System.out.println(cache.getVideo("video_123")); // L3 → L2
        System.out.println(cache.getVideo("video_123")); // L2
        System.out.println(cache.getVideo("video_123")); // promote to L1

        cache.stats();
    }
}