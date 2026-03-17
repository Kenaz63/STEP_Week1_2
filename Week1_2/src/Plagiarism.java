import java.util.*;

public class Plagiarism {

    private Map<String, Set<String>> index;
    private Map<String, List<String>> documents;
    private int n = 5;

    public Plagiarism() {
        index = new HashMap<>();
        documents = new HashMap<>();
    }

    public void addDocument(String docId, String content) {
        List<String> ngrams = generateNGrams(content);
        documents.put(docId, ngrams);

        for (String gram : ngrams) {
            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docId);
        }
    }

    private List<String> generateNGrams(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }
            ngrams.add(sb.toString().trim());
        }

        return ngrams;
    }

    public void analyzeDocument(String docId) {
        List<String> target = documents.get(docId);
        if (target == null) return;

        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : target) {
            if (index.containsKey(gram)) {
                for (String otherDoc : index.get(gram)) {
                    if (!otherDoc.equals(docId)) {
                        matchCount.put(otherDoc, matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        int total = target.size();

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            String otherDoc = entry.getKey();
            int matches = entry.getValue();
            double similarity = (matches * 100.0) / total;

            System.out.println("Compared with: " + otherDoc +
                    " | Matching n-grams: " + matches +
                    " | Similarity: " + String.format("%.2f", similarity) + "%");
        }
    }

    public static void main(String[] args) {
        Plagiarism p = new Plagiarism();

        p.addDocument("doc1", "this is a sample document with some text data for testing plagiarism detection system");
        p.addDocument("doc2", "this is a sample document with some different text data used for testing system");
        p.addDocument("doc3", "completely unrelated content that does not match with other documents here");

        p.analyzeDocument("doc1");
    }
}