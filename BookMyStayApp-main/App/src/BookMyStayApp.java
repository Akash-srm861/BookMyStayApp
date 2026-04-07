// Version: 6.1 (refactored)

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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

// Version: 6.0
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
     * @param reservation booking request
     * @param inventory   centralized room inventory
     */
    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        int available = availability.getOrDefault(roomType, 0);
        if (available <= 0) {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName()
                    + " - No availability for " + roomType);
            return;
        }

        String roomId = generateRoomId(roomType);
        allocatedRoomIds.add(roomId);

        assignedRoomsByType.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);

        inventory.updateAvailability(roomType, available - 1);

        System.out.println("Booking confirmed for Guest: " + reservation.getGuestName()
                + ", Room ID: " + roomId);
    }

    /**
     * Generates a unique room ID for the given room type.
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

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Room Allocation Processing");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        while (bookingQueue.hasPendingRequests()) {
            allocationService.allocateRoom(bookingQueue.getNextRequest(), inventory);
        }
    }
}
