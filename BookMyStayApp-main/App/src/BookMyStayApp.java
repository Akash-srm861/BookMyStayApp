// Version: 11.0 (Concurrent Booking Simulation)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

abstract class Room {
    private int numberOfBeds;
    private double size;
    private double pricePerNight;

    public Room(int numberOfBeds, double size, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.size = size;
        this.pricePerNight = pricePerNight;
    }

    public int getNumberOfBeds() { return numberOfBeds; }
    public double getSize() { return size; }
    public double getPricePerNight() { return pricePerNight; }

    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println(getRoomType() + ":");
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + (int) size + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 250, 1500.0); }
    @Override
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 400, 2500.0); }
    @Override
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 750, 5000.0); }
    @Override
    public String getRoomType() { return "Suite Room"; }
}

// Version: 3.0
class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

// Version: 4.0
class RoomSearchService {
    public void searchAvailableRooms(
            RoomInventory inventory,
            Room singleRoom,
            Room doubleRoom,
            Room suiteRoom) {

        Map<String, Integer> availability = inventory.getRoomAvailability();
        boolean anyAvailable = false;

        System.out.println("Available Rooms:");
        System.out.println();

        if (availability.get("Single Room") > 0) {
            singleRoom.displayDetails();
            System.out.println("Available: " + availability.get("Single Room"));
            anyAvailable = true;
        }
        if (availability.get("Double Room") > 0) {
            if (anyAvailable) System.out.println();
            doubleRoom.displayDetails();
            System.out.println("Available: " + availability.get("Double Room"));
            anyAvailable = true;
        }
        if (availability.get("Suite Room") > 0) {
            if (anyAvailable) System.out.println();
            suiteRoom.displayDetails();
            System.out.println("Available: " + availability.get("Suite Room"));
            anyAvailable = true;
        }

        if (!anyAvailable) {
            System.out.println("No rooms currently available.");
        }
    }
}

// Version: 5.0
class Reservation {
    /** Name of the guest making the booking. */
    private String guestName;
    /** Requested room type. */
    private String roomType;

    /**
     * Creates a new booking request.
     * @param guestName name of the guest
     * @param roomType  requested room type
     */
    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    /** @return guest name */
    public String getGuestName() { return guestName; }
    /** @return requested room type */
    public String getRoomType() { return roomType; }
}

// Version: 5.0
class BookingRequestQueue {
    /** Queue that stores booking requests. */
    private Queue<Reservation> requestQueue;

    /** Initializes an empty booking queue. */
    public BookingRequestQueue() { requestQueue = new LinkedList<>(); }

    /**
     * Adds a booking request to the queue.
     * @param reservation booking request
     */
    public void cancelBooking(String reservationId, RoomInventory inventory) {
        // Step 1 — Reject if the reservation was never registered or was already cancelled.
        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Cancellation failed: Reservation "
                    + reservationId + " not found or already cancelled.");
            return;
        }

        // Step 2 — Resolve room type before mutating state.
        String roomType = reservationRoomTypeMap.get(reservationId);

        // Step 3 — Record the release on the rollback stack (LIFO).
        releasedRoomIds.push(reservationId);

        // Step 4 — Remove from the active map; prevents duplicate cancellation.
        reservationRoomTypeMap.remove(reservationId);

        // Step 5 — Restore inventory count for the released room type.
        int current = inventory.getRoomAvailability().getOrDefault(roomType, 0);
        inventory.updateAvailability(roomType, current + 1);

        System.out.println("Booking cancelled successfully. "
                + "Inventory restored for room type: " + roomType);
    }

    /**
     * Displays recently cancelled reservations in LIFO order.
     * Pops each entry from the stack so the most recent cancellation
     * is always shown first, visualising true rollback ordering.
     * Once displayed, the stack is fully drained.
     */
    public void showRollbackHistory() {
        System.out.println("Rollback History (Most Recent First):");
        while (!releasedRoomIds.isEmpty()) {
            System.out.println("Released Reservation ID: " + releasedRoomIds.pop());
        }
    }
}

// Version: 11.0
/**
 * Processes booking requests from a shared queue in a multi-threaded environment.
 * Implements Runnable so the same processing logic can be executed by any number
 * of threads without subclassing Thread, keeping the design flexible.
 *
 * Thread safety is enforced at two distinct critical sections:
 *   1. Queue access   — synchronized on bookingQueue (one thread dequeues at a time).
 *   2. Allocation     — synchronized on inventory    (one thread allocates at a time).
 *
 * Separating the two locks avoids holding a broad lock across both operations,
 * which would reduce concurrency unnecessarily.
 */
class ConcurrentBookingProcessor implements Runnable {

    /**
     * Shared booking request queue accessed by all processor threads.
     */
    private BookingRequestQueue bookingQueue;

    /**
     * Shared room inventory mutated during allocation.
     */
    private RoomInventory inventory;

    /**
     * Shared allocation service whose internal state must also be protected.
     */
    private RoomAllocationService allocationService;

    /**
     * Creates a new booking processor with references to all shared resources.
     *
     * @param bookingQueue    shared booking queue
     * @param inventory       shared room inventory
     * @param allocationService shared allocation service
     */
    public ConcurrentBookingProcessor(BookingRequestQueue bookingQueue,
                                      RoomInventory inventory,
                                      RoomAllocationService allocationService) {
        this.bookingQueue    = bookingQueue;
        this.inventory       = inventory;
        this.allocationService = allocationService;
    }

    /**
     * Continuously dequeues and processes booking requests until the queue
     * is empty. Each iteration uses two separate synchronized blocks:
     *
     * Block 1 (bookingQueue): Safely retrieves the next pending request.
     *   - hasPendingRequests and getNextRequest are checked atomically so
     *     two threads cannot both see a non-empty queue and dequeue the
     *     same reservation.
     *   - If no request is pending the loop exits cleanly.
     *
     * Block 2 (inventory): Safely allocates the room and updates the count.
     *   - Held only for the duration of the allocation, not across the
     *     dequeue step, so threads can overlap their queue checks.
     */
    @Override
    public void run() {
        while (true) {
            Reservation reservation;

            // Critical section 1 — dequeue one request atomically.
            // Declared outside so it is visible to the allocation block below.
            synchronized (bookingQueue) {
                if (!bookingQueue.hasPendingRequests()) {
                    break; // Queue exhausted; this thread's work is done.
                }
                reservation = bookingQueue.getNextRequest();
            }

            // Critical section 2 — allocate the room atomically.
            // Synchronizing on inventory prevents two threads from reading
            // the same availability count and both decrementing it,
            // which would silently over-allocate rooms.
            synchronized (inventory) {
                allocationService.allocateRoom(reservation, inventory);
            }
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Booking Request Queue");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        Reservation r1 = new Reservation("Abhi", "Single");
        Reservation r2 = new Reservation("Subha", "Double");
        Reservation r3 = new Reservation("Vanmathi", "Suite");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Print remaining inventory in a fixed order so the output is
        // deterministic regardless of thread scheduling.
        System.out.println("Remaining Inventory:");
        System.out.println("Single: "
                + concurrentInventory.getRoomAvailability().get("Single"));
        System.out.println("Double: "
                + concurrentInventory.getRoomAvailability().get("Double"));
        System.out.println("Suite: "
                + concurrentInventory.getRoomAvailability().get("Suite"));
    }
}
