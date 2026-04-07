// Version: 9.0 (Error Handling & Validation)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
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

// Version: 8.0 — roomId added to carry the assigned room ID into booking history
// without changing the two-arg constructor used throughout the queue flow.
class Reservation {
    private String guestName;
    private String roomType;

    /**
     * Assigned room ID, populated by RoomAllocationService after a
     * successful allocation. Null until the booking is confirmed.
     */
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType()  { return roomType;  }
    public String getRoomId()    { return roomId;    }

    /**
     * Called by RoomAllocationService once a room ID is generated.
     * Keeps allocation concerns out of the Reservation constructor.
     *
     * @param roomId assigned room ID (e.g. "Single-1")
     */
    public void setRoomId(String roomId) { this.roomId = roomId; }
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

        // Stamp the assigned ID onto the reservation so it travels into
        // BookingHistory as a self-contained, fully-described record.
        reservation.setRoomId(roomId);

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

// Version: 8.0
/**
 * Maintains an ordered audit trail of confirmed reservations.
 * Uses ArrayList to preserve insertion order, which naturally reflects
 * the chronological sequence in which bookings were confirmed.
 * Read operations are exposed; no mutation of stored records is permitted.
 */
class BookingHistory {
    /**
     * List that stores confirmed reservations in insertion order.
     */
    private List<Reservation> confirmedReservations;

    /**
     * Initializes an empty booking history.
     */
    public BookingHistory() { confirmedReservations = new ArrayList<>(); }

    /**
     * Adds a confirmed reservation to booking history.
     * Called immediately after a successful allocation so the record is
     * captured before any further processing occurs.
     *
     * @param reservation confirmed booking (roomId already set)
     */
    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    /**
     * Returns all confirmed reservations.
     * Callers receive the live list reference; reporting logic must
     * treat this as read-only to satisfy the immutability requirement.
     *
     * @return list of reservations in confirmation order
     */
    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

// Version: 8.0
/**
 * Generates human-readable reports from booking history.
 * Holds no state of its own — all data comes from BookingHistory,
 * so reporting never modifies or reprocesses live booking flows.
 */
class BookingReportService {
    /**
     * Displays a summary report of all confirmed bookings.
     * Iterates the history list in insertion order, printing each
     * reservation's guest name, room type, and assigned room ID.
     * The history list is not modified during report generation.
     *
     * @param history booking history containing confirmed reservations
     */
    public void generateReport(BookingHistory history) {
        List<Reservation> reservations = history.getConfirmedReservations();
        System.out.println("Booking History Report");
        for (Reservation reservation : reservations) {
            System.out.println("Guest: " + reservation.getGuestName()
                    + ", Room Type: " + reservation.getRoomType());
        }
    }
}

// Version: 9.0
/**
 * Domain-specific exception representing an invalid booking scenario.
 * Extends Exception (checked) so callers are forced to handle or
 * declare it, making error paths explicit in every call site.
 */
class InvalidBookingException extends Exception {
    /**
     * Creates an exception with a descriptive error message.
     *
     * @param message error description
     */
    public InvalidBookingException(String message) { super(message); }
}

// Version: 9.0
/**
 * Validates booking input and system state before a reservation is processed.
 * Applies fail-fast design: the first violated rule throws immediately,
 * preventing any further processing on invalid data.
 */
class ReservationValidator {

    /**
     * Valid room types accepted by the system.
     * Exact case match is required — "single" and "SINGLE" are both rejected.
     */
    private static final List<String> VALID_ROOM_TYPES =
            List.of("Single", "Double", "Suite");

    /**
     * Validates booking input provided by the guest.
     * Checks are applied in order: guest name → room type → availability.
     * The first failure throws immediately (fail-fast).
     *
     * @param guestName name of the guest
     * @param roomType  requested room type (must be exactly Single, Double, or Suite)
     * @param inventory centralized inventory used for availability check
     * @throws InvalidBookingException if any validation rule is violated
     */
    public void validate(String guestName,
                         String roomType,
                         RoomInventory inventory) throws InvalidBookingException {

        // Rule 1 — Guest name must not be blank.
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Rule 2 — Room type must exactly match one of the accepted values.
        // Case-sensitive: "single" and "SUITE" are invalid inputs.
        if (!VALID_ROOM_TYPES.contains(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        // Rule 3 — Requested room type must have available inventory.
        int available = inventory.getRoomAvailability().getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new InvalidBookingException(
                    "No rooms available for type: " + roomType);
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {

        // ── Use Case 6: Room Allocation Processing ──────────────────────────
        System.out.println("Room Allocation Processing");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();
        BookingHistory bookingHistory = new BookingHistory();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Double"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        // Capture the room ID for "Abhi" so it can be used in Use Case 7.
        // Successful allocations are also recorded in booking history for Use Case 8.
        String abhiRoomId = null;
        while (bookingQueue.hasPendingRequests()) {
            Reservation next = bookingQueue.getNextRequest();
            String assignedId = allocationService.allocateRoom(next, inventory);
            if (assignedId != null) {
                bookingHistory.addReservation(next);
                if ("Abhi".equals(next.getGuestName())) {
                    abhiRoomId = assignedId;
                }
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

        // ── Use Case 8: Booking History & Reporting ──────────────────────────
        System.out.println("\nBooking History and Reporting");
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(bookingHistory);

        // ── Use Case 9: Error Handling & Validation ───────────────────────────
        System.out.println("\nBooking Validation");
        Scanner scanner = new Scanner(System.in);
        RoomInventory validationInventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            // Validate before touching any booking or inventory state.
            // If validation fails, the exception is thrown here and the
            // catch block handles it — no reservation is created.
            validator.validate(guestName, roomType, validationInventory);

            // Validation passed — safe to queue and process the booking.
            BookingRequestQueue validationQueue = new BookingRequestQueue();
            RoomAllocationService validationAllocator = new RoomAllocationService();
            validationQueue.addRequest(new Reservation(guestName, roomType));
            while (validationQueue.hasPendingRequests()) {
                validationAllocator.allocateRoom(
                        validationQueue.getNextRequest(), validationInventory);
            }
        } catch (InvalidBookingException e) {
            // Domain-specific validation error: display the message and
            // allow the application to continue running safely.
            System.out.println("Booking failed: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
