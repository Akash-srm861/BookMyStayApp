import java.util.HashMap;
import java.util.Map;

/**
 * DOMAIN MODELS
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

    public void displayDetails(int availableCount) {
        System.out.println(type + ":");
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
        System.out.println("Available: " + availableCount);
        System.out.println();
    }
}

class SingleRoom extends Room { public SingleRoom() { super("Single", 1, 250, 1500.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super("Double", 2, 400, 2500.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super("Suite", 3, 750, 5000.0); } }

/**
 * INVENTORY MANAGEMENT
 */
class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }
}

/**
 * SEARCH SERVICE - Use Case 4
 */
class RoomSearchService {
    public void searchAvailableRooms(RoomInventory inventory, Room single, Room doubleRoom, Room suite) {
        Map<String, Integer> availability = inventory.getRoomAvailability();

        System.out.println("Room Search\n");

        // Conditional Check: Only display if availability is > 0
        if (availability.get("Single") > 0) {
            single.displayDetails(availability.get("Single"));
        }

        if (availability.get("Double") > 0) {
            doubleRoom.displayDetails(availability.get("Double"));
        }

        if (availability.get("Suite") > 0) {
            suite.displayDetails(availability.get("Suite"));
        }
    }
}

/**
 * MAIN CLASS - BookMyStayApp
 */
public class BookMyStayApp {
    public static void main(String[] args) {
        // Initialize Core Components
        RoomInventory inventory = new RoomInventory();
        RoomSearchService searchService = new RoomSearchService();

        // Initialize Room Definitions
        Room single = new SingleRoom();
        Room dual = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Execute Search
        searchService.searchAvailableRooms(inventory, single, dual, suite);
    }
}