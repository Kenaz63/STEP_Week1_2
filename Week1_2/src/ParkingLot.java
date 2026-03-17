import java.util.*;

public class ParkingLot {

    private static class Slot {
        String license;
        long entryTime;
        boolean isDeleted;

        Slot(String license) {
            this.license = license;
            this.entryTime = System.currentTimeMillis();
            this.isDeleted = false;
        }
    }

    private Slot[] table;
    private int capacity;
    private int size;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        this.table = new Slot[capacity];
    }

    private int hash(String license) {
        return Math.abs(license.hashCode()) % capacity;
    }

    // Park vehicle using linear probing
    public int parkVehicle(String license) {
        int index = hash(license);
        int probes = 0;

        while (table[index] != null && !table[index].isDeleted) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index] = new Slot(license);
        size++;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
        return index;
    }

    // Exit vehicle
    public void exitVehicle(String license) {
        int index = hash(license);
        int start = index;

        while (table[index] != null) {
            if (!table[index].isDeleted && table[index].license.equals(license)) {

                long durationMs = System.currentTimeMillis() - table[index].entryTime;
                double minutes = durationMs / (1000.0 * 60);

                double fee = minutes * 0.5; // simple rate

                table[index].isDeleted = true;
                size--;

                System.out.println("Spot #" + index + " freed. Duration: "
                        + String.format("%.2f", minutes) + " mins, Fee: $"
                        + String.format("%.2f", fee));
                return;
            }

            index = (index + 1) % capacity;
            if (index == start) break;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot (linear scan)
    public int findNearestSpot() {
        for (int i = 0; i < capacity; i++) {
            if (table[i] == null || table[i].isDeleted) {
                return i;
            }
        }
        return -1;
    }

    public void getStatistics() {
        double occupancy = (size * 100.0) / capacity;
        System.out.println("Occupancy: " + occupancy + "%");
    }

    // Demo
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(10);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}