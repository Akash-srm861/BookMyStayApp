// Version: 3.1 (refactored)

import java.util.HashMap;
import java.util.Map;

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
    /**
     * Stores available room count for each room type.
     * Key   -> Room type name
     * Value -> Available room count
     */
    private Map<String, Integer> roomAvailability;

    /**
     * Constructor initializes the inventory
     * with default availability values.
     */
    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    /**
     * Initializes room availability data.
     * This method centralizes inventory setup
     * instead of using scattered variables.
     */
    private void initializeInventory() {
        roomAvailability.put("Single Room", 5);
        roomAvailability.put("Double Room", 3);
        roomAvailability.put("Suite Room", 2);
    }

    /**
     * Returns the current availability map.
     * @return map of room type to available count
     */
    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }

    /**
     * Updates availability for a specific room type.
     * @param roomType the room type to update
     * @param count    new availability count
     */
    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };
        RoomInventory inventory = new RoomInventory();

        System.out.println("Hotel Room Inventory");

        for (int i = 0; i < rooms.length; i++) {
            if (i > 0) System.out.println();
            rooms[i].displayDetails();
            System.out.println("Available: " + inventory.getRoomAvailability().get(rooms[i].getRoomType()));
        }
    }
}
