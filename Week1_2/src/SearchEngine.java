import java.util.*;

public class SearchEngine {

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        PriorityQueue<String> topQueries = new PriorityQueue<>(
                (a, b) -> freq.get(a) - freq.get(b) // min-heap
        );
    }

    private static final int TOP_K = 10;
    private static Map<String, Integer> freq = new HashMap<>();
    private TrieNode root;

    public SearchEngine() {
        root = new TrieNode();
    }

    // Insert or update query
    public void updateFrequency(String query) {
        freq.put(query, freq.getOrDefault(query, 0) + 1);
        insertIntoTrie(query);
    }

    private void insertIntoTrie(String query) {
        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            addToTopK(node, query);
        }
    }

    private void addToTopK(TrieNode node, String query) {
        if (!node.topQueries.contains(query)) {
            node.topQueries.offer(query);
        }

        if (node.topQueries.size() > TOP_K) {
            node.topQueries.poll(); // remove smallest freq
        }
    }

    // Search top suggestions
    public List<String> search(String prefix) {
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }

        List<String> result = new ArrayList<>(node.topQueries);

        // sort descending by frequency
        result.sort((a, b) -> freq.get(b) - freq.get(a));

        return result;
    }

    // Demo
    public static void main(String[] args) {
        SearchEngine se = new SearchEngine();

        se.updateFrequency("java tutorial");
        se.updateFrequency("java tutorial");
        se.updateFrequency("javascript");
        se.updateFrequency("java download");

        System.out.println(se.search("jav"));
    }
}