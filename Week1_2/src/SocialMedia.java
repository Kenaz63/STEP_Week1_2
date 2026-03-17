import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SocialMedia {

    private Set<String> usernames;
    private Map<String, Integer> attemptCount;

    public SocialMedia() {
        usernames = ConcurrentHashMap.newKeySet();
        attemptCount = new ConcurrentHashMap<>();
    }

    public boolean checkAvailability(String username) {
        attemptCount.put(username, attemptCount.getOrDefault(username, 0) + 1);
        return !usernames.contains(username);
    }

    public boolean registerUser(String username) {
        if (checkAvailability(username)) {
            usernames.add(username);
            return true;
        }
        return false;
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernames.contains(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String dotVersion = username.replace("_", ".");
        if (!usernames.contains(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String result = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptCount.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result + " (" + max + " attempts)";
    }

    public static void main(String[] args) {
        SocialMedia sm = new SocialMedia();

        sm.registerUser("john_doe");

        System.out.println(sm.checkAvailability("john_doe"));
        System.out.println(sm.checkAvailability("jane_smith"));

        System.out.println(sm.suggestAlternatives("john_doe"));

        for (int i = 0; i < 5; i++) sm.checkAvailability("admin");
        for (int i = 0; i < 3; i++) sm.checkAvailability("user");

        System.out.println(sm.getMostAttempted());
    }
}