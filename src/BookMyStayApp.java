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
 * <p>
 * UC4: Room Search and Availability Check (Read-Only)
 *
 * @author Akash
 * @version 4.0
 */
public class BookMyStayApp {

    // =========================================================================
    // UC2 — Room Domain Model
    // =========================================================================

    static abstract class Room {
        protected String roomType, sizeDescription, amenities;
        protected int numberOfBeds;
        protected double pricePerNight;

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

        public String getAmenities() {
            return amenities;
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

    static class RoomInventory {
        private final HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 0); // intentionally zero to test UC4 filter
            inventory.put("Suite Room", 2);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public Iterable<String> getRoomTypes() {
            return inventory.keySet();
        }

        public void displayInventory() {
            System.out.println("\n  --- Room Inventory ---");
            for (Map.Entry<String, Integer> e : inventory.entrySet())
                System.out.printf("  %-15s : %d available%n", e.getKey(), e.getValue());
            System.out.println("  ----------------------");
        }
    }

    // =========================================================================
    // UC4 — Search Service (Read-Only Access, No State Mutation)
    // =========================================================================

    /**
     * SearchService — provides guest-facing room search.
     *
     * <p>
     * Reads from inventory and room domain objects exclusively.
     * Never modifies inventory state. Filters out room types with
     * zero availability before displaying results (defensive programming).
     */
    static class SearchService {
        private final RoomInventory inventory;
        private final Map<String, Room> roomCatalog;

        public SearchService(RoomInventory inventory) {
            this.inventory = inventory;
            this.roomCatalog = new HashMap<>();
            roomCatalog.put("Single Room", new SingleRoom());
            roomCatalog.put("Double Room", new DoubleRoom());
            roomCatalog.put("Suite Room", new SuiteRoom());
        }

        /**
         * Read-only search — displays available rooms without changing inventory.
         */
        public void searchAvailableRooms() {
            System.out.println("\n  --- Available Rooms ---");
            boolean anyFound = false;
            for (String type : inventory.getRoomTypes()) {
                int count = inventory.getAvailability(type); // read-only
                if (count <= 0) {
                    System.out.println("  [SKIP] " + type + " — not available");
                    continue;
                }
                anyFound = true;
                Room room = roomCatalog.get(type);
                if (room != null) {
                    System.out.println();
                    room.displayRoomDetails();
                    System.out.println("  Available   : " + count + " rooms");
                }
            }
            if (!anyFound)
                System.out.println("  No rooms currently available.");
            System.out.println("\n  -----------------------");
        }
    }

    // =========================================================================
    // Application Entry Point (main)
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking Management System v4.0");
        System.out.println("============================================");

        // UC3: Centralized inventory
        RoomInventory inventory = new RoomInventory();
        System.out.println("\n[UC3] Initial Inventory:");
        inventory.displayInventory();

        // UC4: Read-only room search
        System.out.println("\n[UC4] Guest Room Search (Read-Only):");
        SearchService search = new SearchService(inventory);
        search.searchAvailableRooms();

        // UC4: Verify inventory unchanged after search
        System.out.println("\n[UC4] Inventory after search (must be unchanged):");
        inventory.displayInventory();

        System.out.println("\n============================================");
        System.out.println("  Search completed. Inventory NOT modified.");
        System.out.println("  Read-only access prevents state corruption.");
        System.out.println("============================================");
    }
}
