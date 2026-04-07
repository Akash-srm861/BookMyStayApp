// Version: 5.1 (refactored)

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
    public void addRequest(Reservation reservation) { requestQueue.offer(reservation); }

    /**
     * Retrieves and removes the next booking request from the queue.
     * @return next reservation request
     */
    public Reservation getNextRequest() { return requestQueue.poll(); }

    /**
     * Checks whether there are pending booking requests.
     * @return true if queue is not empty
     */
    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
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

        while (bookingQueue.hasPendingRequests()) {
            Reservation r = bookingQueue.getNextRequest();
            System.out.println("Guest: " + r.getGuestName() + " | Room Type: " + r.getRoomType());
        }
    }
}
