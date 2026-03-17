import java.util.concurrent.*;

public class ApiGateway {
    static class TokenBucket {
        long tokens;
        long lastRefillTime;
        final long maxTokens;
        final long refillRate;

        TokenBucket(long maxTokens, long refillRate) {
            this.tokens = maxTokens;
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.lastRefillTime = System.currentTimeMillis();
        }
    }

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final long maxTokens = 1000;
    private final long refillRate = 1000;

    public boolean checkRateLimit(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(maxTokens, refillRate));

        synchronized (bucket) {
            refill(bucket);
            if (bucket.tokens > 0) {
                bucket.tokens--;
                return true;
            }
            return false;
        }
    }

    private void refill(TokenBucket bucket) {
        long now = System.currentTimeMillis();
        long elapsed = now - bucket.lastRefillTime;
        long tokensToAdd = (elapsed * bucket.refillRate) / 3600000;
        if (tokensToAdd > 0) {
            bucket.tokens = Math.min(bucket.maxTokens, bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;
        }
    }

    public String getStatus(String clientId) {
        TokenBucket bucket = buckets.get(clientId);
        if (bucket == null) return "No data";
        return "tokens=" + bucket.tokens;
    }
}