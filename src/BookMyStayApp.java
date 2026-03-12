import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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
 * <p>
 * UC5: Booking Request Queue (First-Come-First-Served)
 *
 * @author Akash
 * @version 5.0
 */
public class BookMyStayApp {

    // =========================================================================
    // UC2 — Room Domain Model
    // =========================================================================

    static abstract class Room {
        protected String roomType, sizeDescription, amenities;
        protected int numberOfBeds;
        protected double pricePerNight;

        public Room(String rt, int beds, double price, String size, String am) {
            roomType = rt;
            numberOfBeds = beds;
            pricePerNight = price;
            sizeDescription = size;
            amenities = am;
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
            inventory.put("Single Room", 3);
            inventory.put("Double Room", 2);
            inventory.put("Suite Room", 1);
        }

        public int getAvailability(String t) {
            return inventory.getOrDefault(t, 0);
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
    // UC4 — Search Service (Read-Only)
    // =========================================================================

    static class SearchService {
        private final RoomInventory inventory;
        private final Map<String, Room> catalog;

        public SearchService(RoomInventory inv) {
            inventory = inv;
            catalog = new HashMap<>();
            catalog.put("Single Room", new SingleRoom());
            catalog.put("Double Room", new DoubleRoom());
            catalog.put("Suite Room", new SuiteRoom());
        }

        public void searchAvailableRooms() {
            System.out.println("\n  --- Available Rooms ---");
            boolean found = false;
            for (String t : inventory.getRoomTypes()) {
                int cnt = inventory.getAvailability(t);
                if (cnt <= 0) {
                    System.out.println("  [SKIP] " + t + " — unavailable");
                    continue;
                }
                found = true;
                Room r = catalog.get(t);
                if (r != null) {
                    System.out.println();
                    r.displayRoomDetails();
                    System.out.println("  Available   : " + cnt + " rooms");
                }
            }
            if (!found)
                System.out.println("  No rooms currently available.");
            System.out.println("\n  -----------------------");
        }
    }

    // =========================================================================
    // UC5 — Booking Request Queue (FIFO)
    // =========================================================================

    /**
     * Reservation — immutable record of a guest's booking intent.
     * Does NOT modify any inventory state.
     */
    static class Reservation {
        private final String guestName, roomType;
        private final int numberOfNights;

        public Reservation(String guest, String room, int nights) {
            guestName = guest;
            roomType = room;
            numberOfNights = nights;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRoomType() {
            return roomType;
        }

        public int getNumberOfNights() {
            return numberOfNights;
        }

        @Override
        public String toString() {
            return "Reservation[guest=" + guestName + ", room=" + roomType
                    + ", nights=" + numberOfNights + "]";
        }
    }

    /**
     * BookingRequestQueue — FIFO intake for incoming reservations.
     *
     * <p>
     * Uses a {@code LinkedList}-backed {@code Queue}. Requests arrive
     * at the tail and are processed from the head (FIFO). Guarantees
     * fairness: no request can bypass another regardless of arrival timing.
     * No room allocation or inventory change occurs at this stage.
     */
    static class BookingRequestQueue {
        private final Queue<Reservation> queue;

        public BookingRequestQueue() {
            queue = new LinkedList<>();
        }

        public void addRequest(Reservation r) {
            queue.offer(r);
            System.out.println("  [QUEUED] " + r);
        }

        public void displayQueue() {
            System.out.println("\n  --- Pending Requests (FIFO Order) ---");
            int pos = 1;
            for (Reservation r : queue)
                System.out.println("  " + pos++ + ". " + r);
            System.out.println("  Total pending: " + queue.size());
            System.out.println("  ------------------------------------");
        }

        public Queue<Reservation> getQueue() {
            return queue;
        }

        public int getPendingCount() {
            return queue.size();
        }

        public Reservation peekNextRequest() {
            return queue.peek();
        }
    }

    // =========================================================================
    // Application Entry Point (main)
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking Management System v5.0");
        System.out.println("============================================");

        // UC3: Inventory
        RoomInventory inventory = new RoomInventory();
        System.out.println("\n[UC3] Inventory:");
        inventory.displayInventory();

        // UC4: Search
        System.out.println("\n[UC4] Room Search (Read-Only):");
        new SearchService(inventory).searchAvailableRooms();

        // UC5: Booking queue
        System.out.println("\n[UC5] Submitting Booking Requests:");
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        bookingQueue.addRequest(new Reservation("Alice Johnson", "Single Room", 3));
        bookingQueue.addRequest(new Reservation("Bob Smith", "Suite Room", 2));
        bookingQueue.addRequest(new Reservation("Carol Williams", "Double Room", 5));
        bookingQueue.addRequest(new Reservation("David Brown", "Single Room", 1));
        bookingQueue.addRequest(new Reservation("Eva Martinez", "Suite Room", 4));

        bookingQueue.displayQueue();

        System.out.println("\n[UC5] Next request (peek, no removal):");
        System.out.println("  -> " + bookingQueue.peekNextRequest());

        System.out.println("\n============================================");
        System.out.println("  " + bookingQueue.getPendingCount() + " requests queued in FIFO order.");
        System.out.println("  No inventory modified. Queue ready for UC6.");
        System.out.println("============================================");
    }
}
