import java.util.*;

public class FinancialTransaction {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long time; // epoch minutes

        Transaction(int id, int amount, String merchant, String account, long time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    // 1. Classic Two Sum
    public List<int[]> findTwoSum(List<Transaction> txns, int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : txns) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // 2. Two Sum within time window (1 hour)
    public List<int[]> findTwoSumWithTime(List<Transaction> txns, int target) {
        List<int[]> result = new ArrayList<>();
        txns.sort(Comparator.comparingLong(t -> t.time));

        Map<Integer, List<Transaction>> map = new HashMap<>();
        int left = 0;

        for (Transaction t : txns) {

            // Remove old transactions (older than 60 mins)
            while (t.time - txns.get(left).time > 60) {
                Transaction old = txns.get(left);
                List<Transaction> list = map.get(old.amount);
                list.remove(old);
                if (list.isEmpty()) map.remove(old.amount);
                left++;
            }

            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction match : map.get(complement)) {
                    result.add(new int[]{match.id, t.id});
                }
            }

            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }

        return result;
    }

    // 3. K-Sum (generalized)
    public List<List<Integer>> findKSum(List<Transaction> txns, int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        txns.sort(Comparator.comparingInt(t -> t.amount));

        kSumHelper(txns, 0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void kSumHelper(List<Transaction> txns, int start, int k, int target,
                            List<Integer> current, List<List<Integer>> result) {

        if (k == 2) {
            int left = start, right = txns.size() - 1;

            while (left < right) {
                int sum = txns.get(left).amount + txns.get(right).amount;

                if (sum == target) {
                    List<Integer> temp = new ArrayList<>(current);
                    temp.add(txns.get(left).id);
                    temp.add(txns.get(right).id);
                    result.add(temp);
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
            return;
        }

        for (int i = start; i < txns.size(); i++) {
            current.add(txns.get(i).id);
            kSumHelper(txns, i + 1, k - 1, target - txns.get(i).amount, current, result);
            current.remove(current.size() - 1);
        }
    }

    // 4. Duplicate detection
    public List<List<Transaction>> detectDuplicates(List<Transaction> txns) {
        Map<String, List<Transaction>> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : txns) {
            String key = t.amount + "-" + t.merchant;
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        for (List<Transaction> group : map.values()) {
            Set<String> accounts = new HashSet<>();
            for (Transaction t : group) accounts.add(t.account);

            if (accounts.size() > 1) {
                result.add(group);
            }
        }

        return result;
    }

    // Demo
    public static void main(String[] args) {
        FinancialTransaction ft = new FinancialTransaction();

        List<Transaction> txns = Arrays.asList(
                new Transaction(1, 500, "StoreA", "acc1", 600),
                new Transaction(2, 300, "StoreB", "acc2", 615),
                new Transaction(3, 200, "StoreC", "acc3", 630),
                new Transaction(4, 500, "StoreA", "acc4", 640)
        );

        System.out.println(ft.findTwoSum(txns, 500));
        System.out.println(ft.findTwoSumWithTime(txns, 500));
        System.out.println(ft.findKSum(txns, 3, 1000));
        System.out.println(ft.detectDuplicates(txns));
    }
}