import java.util.HashMap;
import java.util.Map;

/**
 * BookMyStayApp - Hotel Booking Management System.
 *
 * <p>
 * UC1: Application Entry and Welcome Message
 * <p>
 * UC2: Basic Room Types and Static Availability
 * <p>
 * UC3: Centralized Room Inventory Management (HashMap)
 *
 * @author Sharveswar
 * @version 3.0
 */
public class BookMyStayApp {

    // =========================================================================
    // UC2 — Room Domain Model
    // =========================================================================

    static abstract class Room {
        protected String roomType;
        protected int numberOfBeds;
        protected double pricePerNight;
        protected String sizeDescription;
        protected String amenities;

        public Room(String roomType, int numberOfBeds, double pricePerNight,
                String sizeDescription, String amenities) {
            this.roomType = roomType;
            this.numberOfBeds = numberOfBeds;
            this.pricePerNight = pricePerNight;
            this.sizeDescription = sizeDescription;
            this.amenities = amenities;
        }

        public String getRoomType() {
            return roomType;
        }

        public double getPricePerNight() {
            return pricePerNight;
        }

        public int getNumberOfBeds() {
            return numberOfBeds;
        }

        public String getSizeDescription() {
            return sizeDescription;
        }

        public abstract void displayAmenities();

        public void displayRoomDetails() {
            System.out.println("  Room Type   : " + roomType);
            System.out.println("  Beds        : " + numberOfBeds);
            System.out.println("  Size        : " + sizeDescription);
            System.out.printf("  Price/Night : $%.2f%n", pricePerNight);
            displayAmenities();
        }
    }

    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 80.00, "Small (200 sq ft)", "Wi-Fi, TV, Mini-Fridge");
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities   : " + amenities);
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 140.00, "Medium (350 sq ft)", "Wi-Fi, TV, Mini-Fridge, Work Desk");
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities   : " + amenities);
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 280.00, "Large (600 sq ft)", "Wi-Fi, TV, Mini-Bar, Jacuzzi, Living Area, Balcony");
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities   : " + amenities);
        }
    }

    // =========================================================================
    // UC3 — Centralized Room Inventory (HashMap)
    // =========================================================================

    /**
     * RoomInventory — single source of truth for room availability.
     *
     * <p>
     * Replaces UC2's scattered availability variables with a
     * centralized {@code HashMap<String, Integer>} providing O(1)
     * access and controlled, consistent updates.
     */
    static class RoomInventory {
        private final HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room", 2);
        }

        /** O(1) availability lookup. */
        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        /** Controlled update — prevents negative counts. */
        public void updateAvailability(String roomType, int newCount) {
            if (inventory.containsKey(roomType) && newCount >= 0)
                inventory.put(roomType, newCount);
        }

        /** Returns all registered room type keys for iteration. */
        public Iterable<String> getRoomTypes() {
            return inventory.keySet();
        }

        /** Prints the full inventory state. */
        public void displayInventory() {
            System.out.println("\n  --- Room Inventory ---");
            for (Map.Entry<String, Integer> e : inventory.entrySet())
                System.out.printf("  %-15s : %d available%n", e.getKey(), e.getValue());
            System.out.println("  ----------------------");
        }
    }

    // =========================================================================
    // Application Entry Point (main)
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking Management System v3.0");
        System.out.println("============================================");

        // UC2: Room catalog
        System.out.println("\n[UC2] Room Type Catalog:");
        Room[] catalog = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };
        for (Room r : catalog) {
            System.out.println();
            r.displayRoomDetails();
        }

        // UC3: Centralized inventory
        System.out.println("\n[UC3] Centralized Inventory (HashMap):");
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        System.out.println("\n[UC3] Availability check (O(1) lookup):");
        System.out.println("  Single Room: " + inventory.getAvailability("Single Room"));
        System.out.println("  Double Room: " + inventory.getAvailability("Double Room"));
        System.out.println("  Suite Room : " + inventory.getAvailability("Suite Room"));

        System.out.println("\n[UC3] Controlled update:");
        inventory.updateAvailability("Single Room", 4);
        inventory.displayInventory();

        System.out.println("\n============================================");
        System.out.println("  HashMap ensures O(1) access and single");
        System.out.println("  source of truth for all availability.");
        System.out.println("============================================");
    }
}
