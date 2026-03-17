import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ECommerce {

    private Map<String, AtomicInteger> stock;
    private Map<String, Queue<Integer>> waitingList;

    public ECommerce() {
        stock = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    public void addProduct(String productId, int quantity) {
        stock.put(productId, new AtomicInteger(quantity));
        waitingList.put(productId, new LinkedList<>());
    }

    public int checkStock(String productId) {
        return stock.getOrDefault(productId, new AtomicInteger(0)).get();
    }

    public String purchaseItem(String productId, int userId) {
        stock.putIfAbsent(productId, new AtomicInteger(0));
        waitingList.putIfAbsent(productId, new LinkedList<>());

        AtomicInteger currentStock = stock.get(productId);

        synchronized (currentStock) {
            if (currentStock.get() > 0) {
                int remaining = currentStock.decrementAndGet();
                return "Success, " + remaining + " units remaining";
            } else {
                Queue<Integer> queue = waitingList.get(productId);
                queue.add(userId);
                return "Added to waiting list, position " + queue.size();
            }
        }
    }

    public List<Integer> getWaitingList(String productId) {
        return new ArrayList<>(waitingList.getOrDefault(productId, new LinkedList<>()));
    }

    public static void main(String[] args) {
        ECommerce ec = new ECommerce();

        ec.addProduct("IPHONE15_256GB", 3);

        System.out.println(ec.checkStock("IPHONE15_256GB"));

        System.out.println(ec.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(ec.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println(ec.purchaseItem("IPHONE15_256GB", 11111));
        System.out.println(ec.purchaseItem("IPHONE15_256GB", 99999));

        System.out.println(ec.getWaitingList("IPHONE15_256GB"));
    }
}