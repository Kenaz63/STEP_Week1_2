import java.util.*;

public class DNS {

    class DNSEntry {
        String domain;
        String ip;
        long expiryTime;

        DNSEntry(String domain, String ip, long ttl) {
            this.domain = domain;
            this.ip = ip;
            this.expiryTime = System.currentTimeMillis() + ttl;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int capacity;
    private Map<String, DNSEntry> cache;
    private LinkedHashMap<String, DNSEntry> lru;
    private int hits = 0;
    private int misses = 0;

    public DNS(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.lru = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public synchronized String resolve(String domain) {
        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);
            if (!entry.isExpired()) {
                hits++;
                lru.get(domain);
                return entry.ip;
            } else {
                cache.remove(domain);
                lru.remove(domain);
            }
        }

        misses++;
        String ip = queryUpstream(domain);
        put(domain, ip, 300000);
        return ip;
    }

    private void put(String domain, String ip, long ttl) {
        if (cache.size() >= capacity) {
            String eldest = lru.keySet().iterator().next();
            cache.remove(eldest);
            lru.remove(eldest);
        }

        DNSEntry entry = new DNSEntry(domain, ip, ttl);
        cache.put(domain, entry);
        lru.put(domain, entry);
    }

    private String queryUpstream(String domain) {
        return "172.217.14." + new Random().nextInt(255);
    }

    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }

    public static void main(String[] args) throws InterruptedException {
        DNS dns = new DNS(3);

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));

        Thread.sleep(1000);

        System.out.println(dns.resolve("openai.com"));
        System.out.println(dns.resolve("github.com"));
        System.out.println(dns.resolve("stackoverflow.com"));

        System.out.println(dns.getCacheStats());
    }
}