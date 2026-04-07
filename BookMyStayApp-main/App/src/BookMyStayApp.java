// Version: 7.0 (Add-On Service Selection)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

// Version: 4.0
class RoomSearchService {
    public void searchAvailableRooms(RoomInventory inventory,
                                     Room singleRoom, Room doubleRoom, Room suiteRoom) {
        Map<String, Integer> availability = inventory.getRoomAvailability();
        System.out.println("Available Rooms:");
        boolean anyAvailable = false;
        if (availability.get("Single") > 0) {
            singleRoom.displayDetails();
            System.out.println("Available: " + availability.get("Single"));
            anyAvailable = true;
        }
        if (availability.get("Double") > 0) {
            if (anyAvailable) System.out.println();
            doubleRoom.displayDetails();
            System.out.println("Available: " + availability.get("Double"));
            anyAvailable = true;
        }
        if (availability.get("Suite") > 0) {
            if (anyAvailable) System.out.println();
            suiteRoom.displayDetails();
            System.out.println("Available: " + availability.get("Suite"));
            anyAvailable = true;
        }
        if (!anyAvailable) System.out.println("No rooms currently available.");
    }
}

// Version: 5.0
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// Version: 5.0
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() { requestQueue = new LinkedList<>(); }

    public void addRequest(Reservation reservation) { requestQueue.offer(reservation); }

    public Reservation getNextRequest() { return requestQueue.poll(); }

    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
}

// Version: 6.1 — allocateRoom now returns the assigned room ID so callers
// can use it (e.g. for add-on service mapping) without exposing internal state.
class RoomAllocationService {
    /**
     * Stores all allocated room IDs to prevent duplicate assignments.
     */
    private Set<String> allocatedRoomIds;

    /**
     * Stores assigned room IDs by room type.
     * Key -> Room type | Value -> Set of assigned room IDs
     */
    private Map<String, Set<String>> assignedRoomsByType;

    /** Initializes allocation tracking structures. */
    public RoomAllocationService() {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
    }

    /**
     * Confirms a booking request by assigning a unique room ID and updating inventory.
     *
     * @param reservation booking request
     * @param inventory   centralized room inventory
     * @return assigned room ID, or null if allocation failed
     */
    public String allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        int available = availability.getOrDefault(roomType, 0);
        if (available <= 0) {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName()
                    + " - No availability for " + roomType);
            return null;
        }

        String roomId = generateRoomId(roomType);
        allocatedRoomIds.add(roomId);

        assignedRoomsByType.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);

        inventory.updateAvailability(roomType, available - 1);

        System.out.println("Booking confirmed for Guest: " + reservation.getGuestName()
                + ", Room ID: " + roomId);

        return roomId;
    }

    /**
     * Generates a unique room ID for the given room type.
     *
     * @param roomType type of room
     * @return unique room ID
     */
    private String generateRoomId(String roomType) {
        int count = assignedRoomsByType.containsKey(roomType)
                ? assignedRoomsByType.get(roomType).size() + 1
                : 1;
        return roomType + "-" + count;
    }
}

// Version: 7.0
/**
 * Represents an optional service a guest can attach to a reservation.
 * Modeled as a standalone value object — no dependency on Room or Reservation.
 */
class AddOnService {
    /**
     * Name of the service.
     */
    private String serviceName;

    /**
     * Cost of the service.
     */
    private double cost;

    /**
     * Creates a new add-on service.
     *
     * @param serviceName name of the service
     * @param cost        cost of the service
     */
    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    /**
     * @return service name
     */
    public String getServiceName() { return serviceName; }

    /**
     * @return service cost
     */
    public double getCost() { return cost; }
}

// Version: 7.0
/**
 * Manages the one-to-many relationship between reservations and their add-on services.
 * Operates independently of RoomInventory and RoomAllocationService so that
 * optional features never touch core booking state.
 */
class AddOnServiceManager {
    /**
     * Maps reservation ID to selected services.
     *
     * Key   -> Reservation ID (e.g. "Single-1")
     * Value -> List of selected add-on services (insertion order preserved)
     */
    private Map<String, List<AddOnService>> servicesByReservation;

    /**
     * Initializes the service manager with an empty mapping.
     */
    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    /**
     * Attaches a service to a reservation.
     * Uses computeIfAbsent so the list is created lazily on first access,
     * keeping the map free of empty entries until a service is actually added.
     *
     * @param reservationId confirmed reservation ID (returned by allocateRoom)
     * @param service       add-on service to attach
     */
    public void addService(String reservationId, AddOnService service) {
        servicesByReservation
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    /**
     * Calculates total add-on cost for a reservation by summing
     * the cost of every attached service.
     *
     * @param reservationId reservation ID
     * @return total service cost, or 0.0 if no services are attached
     */
    public double calculateTotalServiceCost(String reservationId) {
        List<AddOnService> services = servicesByReservation.get(reservationId);
        if (services == null) return 0.0;

        double total = 0.0;
        for (AddOnService service : services) {
            total += service.getCost();
        }
        return total;
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };
        RoomInventory inventory = new RoomInventory();

        System.out.println("Hotel Room Inventory");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        // Capture the room ID for "Abhi" so it can be used in Use Case 7.
        // allocateRoom now returns the assigned ID (null on failure).
        String abhiRoomId = null;
        while (bookingQueue.hasPendingRequests()) {
            Reservation next = bookingQueue.getNextRequest();
            String assignedId = allocationService.allocateRoom(next, inventory);
            if ("Abhi".equals(next.getGuestName())) {
                abhiRoomId = assignedId;
            }
        }

        // ── Use Case 7: Add-On Service Selection ────────────────────────────
        System.out.println("\nAdd-On Service Selection");

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Guest selects optional services for their confirmed reservation.
        // Core booking and inventory state are not touched here.
        serviceManager.addService(abhiRoomId, new AddOnService("Breakfast", 500.0));
        serviceManager.addService(abhiRoomId, new AddOnService("Airport Transfer", 1000.0));

        System.out.println("Reservation ID: " + abhiRoomId);
        System.out.println("Total Add-On Cost: "
                + serviceManager.calculateTotalServiceCost(abhiRoomId));
    }
}
