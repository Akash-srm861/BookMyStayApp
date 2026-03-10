/**
 * =============================================================================
 * ABSTRACT CLASS - Room
 * =============================================================================
 */
abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds + " | Size: " + squareFeet + " sqft | Price: " + pricePerNight);
    }
}

/**
 * =============================================================================
 * CHILD CLASSES - SingleRoom and DoubleRoom
 * =============================================================================
 */
class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 250, 1500.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 400, 2500.0);
    }
}

/**
 * =============================================================================
 * MAIN CLASS - BookMyStayApp
 * =============================================================================
 * Use Case 2: Basic Room Types & Static Availability
 * @version 2.1
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.");
        System.out.println("----------------------------------------------");

        // Initializing room types using Polymorphism
        Room single = new SingleRoom();
        Room dual = new DoubleRoom();

        System.out.print("Single Room Details: ");
        single.displayRoomDetails();

        System.out.print("Double Room Details: ");
        dual.displayRoomDetails();
    }
}