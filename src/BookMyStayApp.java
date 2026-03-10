import java.util.HashMap;
import java.util.Map;

/**
 * =============================================================================
 * DOMAIN MODELS - Room Hierarchy
 * =============================================================================
 */
abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;
    protected String type;

    public Room(String type, int numberOfBeds, int squareFeet, double pricePerNight) {
        this.type = type;
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails(int availableCount) {
        System.out.println(type + ":");
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
        System.out.println("Available Rooms: " + availableCount);
        System.out.println();
    }
}

class SingleRoom extends Room { public SingleRoom() { super("Single Room", 1, 250, 1500.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super("Double Room", 2, 400, 2500.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super("Suite Room", 3, 750, 5000.0); } }

/**
 * =============================================================================
 * INVENTORY MANAGEMENT - RoomInventory
 * =============================================================================
 * Use Case 3: Centralized Room Inventory Management
 */
class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        // Centralizing inventory setup instead of scattered variables
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

/**
 * =============================================================================
 * MAIN CLASS - BookMyStayApp
 * =============================================================================
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Hotel Room Inventory Status\n");

        // Initialize Inventory Source of Truth
        RoomInventory inventory = new RoomInventory();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        // Create Room Objects to retrieve characteristics
        Room single = new SingleRoom();
        Room dual = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Display details using data from both the Object and the Inventory Map
        single.displayRoomDetails(availability.get("Single Room"));
        dual.displayRoomDetails(availability.get("Double Room"));
        suite.displayRoomDetails(availability.get("Suite Room"));
    }
}